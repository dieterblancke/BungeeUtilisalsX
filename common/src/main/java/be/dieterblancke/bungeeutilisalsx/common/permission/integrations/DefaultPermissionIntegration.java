package be.dieterblancke.bungeeutilisalsx.common.permission.integrations;

import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultPermissionIntegration implements PermissionIntegration
{

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public CompletableFuture<String> getGroup( final UUID uuid )
    {
        return CompletableFuture.completedFuture( "" );
    }
}
