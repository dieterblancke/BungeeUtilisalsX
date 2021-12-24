package be.dieterblancke.bungeeutilisalsx.common.permission.integrations;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsPermissionIntegration implements PermissionIntegration
{

    @Override
    public boolean isActive()
    {
        return BuX.getInstance().proxyOperations().getPlugin( "LuckPerms" ).isPresent();
    }

    @Override
    public CompletableFuture<String> getGroup( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final net.luckperms.api.model.user.User luckUser = this.loadLuckUser( uuid );

            if ( luckUser == null )
            {
                return "";
            }
            return luckUser.getPrimaryGroup();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @SneakyThrows
    private net.luckperms.api.model.user.User loadLuckUser( final UUID uuid )
    {
        return Optional.ofNullable( net.luckperms.api.LuckPermsProvider.get().getUserManager().getUser( uuid ) )
                .orElse( net.luckperms.api.LuckPermsProvider.get().getUserManager().loadUser( uuid ).get() );
    }
}
