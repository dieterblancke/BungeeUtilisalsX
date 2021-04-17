package be.dieterblancke.bungeeutilisalsx.common.permission.integrations;

import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;

import java.util.UUID;

public class DefaultPermissionIntegration implements PermissionIntegration
{

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public String getGroup( final UUID uuid )
    {
        return "";
    }
}
