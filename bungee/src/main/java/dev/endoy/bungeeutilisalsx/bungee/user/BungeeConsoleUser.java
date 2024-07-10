package dev.endoy.bungeeutilisalsx.bungee.user;

import dev.endoy.bungeeutilisalsx.bungee.BungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ProxyServer;

public class BungeeConsoleUser extends ConsoleUser
{

    @Override
    public void sendMessage( Component component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }

        BungeeUtilisalsX.getInstance().getBungeeAudiences().console().sendMessage( component );
    }

    @Override
    public boolean hasPermission( String permission )
    {
        return ProxyServer.getInstance().getConsole().hasPermission( permission );
    }

    @Override
    public boolean hasPermission( String permission, boolean specific )
    {
        return ProxyServer.getInstance().getConsole().hasPermission( permission );
    }

    @Override
    public boolean hasAnyPermission( String... permissions )
    {
        for ( String permission : permissions )
        {
            if ( ProxyServer.getInstance().getConsole().hasPermission( permission ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void executeCommand( final String command )
    {
        ProxyServer.getInstance().getPluginManager().dispatchCommand( ProxyServer.getInstance().getConsole(), command );
    }

    @Override
    public Audience asAudience()
    {
        return BungeeUtilisalsX.getInstance().getBungeeAudiences().console();
    }
}
