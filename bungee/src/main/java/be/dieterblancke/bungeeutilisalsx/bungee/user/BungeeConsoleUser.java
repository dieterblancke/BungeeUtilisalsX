package be.dieterblancke.bungeeutilisalsx.bungee.user;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

public class BungeeConsoleUser extends ConsoleUser
{

    @Override
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        ProxyServer.getInstance().getConsole().sendMessage( component );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        ProxyServer.getInstance().getConsole().sendMessage( components );
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
}
