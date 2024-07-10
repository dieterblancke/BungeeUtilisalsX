package dev.endoy.bungeeutilisalsx.common.permission.integrations;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.permission.PermissionIntegration;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsPermissionIntegration implements PermissionIntegration
{

    @Override
    public boolean isActive()
    {
        return BuX.getInstance().serverOperations().getPlugin( "LuckPerms" ).isPresent();
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

    @Override
    public String getPrefix( final UUID uuid )
    {
        final net.luckperms.api.model.user.User luckUser = this.loadLuckUser( uuid );

        if ( luckUser == null )
        {
            return "";
        }
        return luckUser.getCachedData().getMetaData().getPrefix();
    }

    @Override
    public String getSuffix( final UUID uuid )
    {
        final net.luckperms.api.model.user.User luckUser = this.loadLuckUser( uuid );

        if ( luckUser == null )
        {
            return "";
        }
        return luckUser.getCachedData().getMetaData().getSuffix();
    }

    private net.luckperms.api.model.user.User loadLuckUser( final UUID uuid )
    {
        return Optional.ofNullable( net.luckperms.api.LuckPermsProvider.get().getUserManager().getUser( uuid ) )
                .orElseGet( () -> net.luckperms.api.LuckPermsProvider.get().getUserManager().loadUser( uuid ).join() );
    }
}
