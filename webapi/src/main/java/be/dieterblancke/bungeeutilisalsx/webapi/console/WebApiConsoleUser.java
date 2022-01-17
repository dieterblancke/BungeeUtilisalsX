package be.dieterblancke.bungeeutilisalsx.webapi.console;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class WebApiConsoleUser extends ConsoleUser
{

    @Override
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        BuX.getLogger().info( ComponentSerializer.toString( component ) );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        BuX.getLogger().info( ComponentSerializer.toString( components ) );
    }

    @Override
    public boolean hasPermission( String permission )
    {
        return true;
    }

    @Override
    public boolean hasPermission( String permission, boolean specific )
    {
        return true;
    }

    @Override
    public boolean hasAnyPermission( String... permissions )
    {
        return true;
    }

    @Override
    public void executeCommand( final String command )
    {
        // do nothing
    }
}
