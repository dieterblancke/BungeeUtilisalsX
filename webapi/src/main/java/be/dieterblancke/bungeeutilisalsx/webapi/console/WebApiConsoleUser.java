package be.dieterblancke.bungeeutilisalsx.webapi.console;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class WebApiConsoleUser extends ConsoleUser
{

    @Override
    public void sendMessage( Component component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        BuX.getLogger().info( LegacyComponentSerializer.legacyAmpersand().serialize( component ) );
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

    @Override
    public Audience asAudience()
    {
        return Audience.empty();
    }
}
