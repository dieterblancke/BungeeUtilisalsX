package be.dieterblancke.bungeeutilisalsx.velocity.user;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import com.velocitypowered.proxy.console.VelocityConsole;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class VelocityConsoleUser extends ConsoleUser
{

    private static org.apache.logging.log4j.Logger velocityConsoleOutput;

    private static org.apache.logging.log4j.Logger getConsoleOutput()
    {
        if ( velocityConsoleOutput == null )
        {
            try
            {
                final Field loggerField = VelocityConsole.class.getDeclaredField( "logger" );
                loggerField.setAccessible( true );

                velocityConsoleOutput = (Logger) loggerField.get( null );
            }
            catch ( IllegalAccessException | NoSuchFieldException e )
            {
                e.printStackTrace();
            }
        }
        return velocityConsoleOutput;
    }

    @Override
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }

        getConsoleOutput().info( BaseComponent.toLegacyText( component ) );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        getConsoleOutput().info( BaseComponent.toLegacyText( components ) );
    }

    @Override
    public boolean hasPermission( String permission )
    {
        return Bootstrap.getInstance().getProxyServer().getConsoleCommandSource().hasPermission( permission );
    }

    @Override
    public boolean hasPermission( String permission, boolean specific )
    {
        return Bootstrap.getInstance().getProxyServer().getConsoleCommandSource().hasPermission( permission );
    }

    @Override
    public boolean hasAnyPermission( String... permissions )
    {
        for ( String permission : permissions )
        {
            if ( Bootstrap.getInstance().getProxyServer().getConsoleCommandSource().hasPermission( permission ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void executeCommand( final String command )
    {
        Bootstrap.getInstance().getProxyServer().getCommandManager().executeAsync(
                Bootstrap.getInstance().getProxyServer().getConsoleCommandSource(),
                command
        );
    }
}
