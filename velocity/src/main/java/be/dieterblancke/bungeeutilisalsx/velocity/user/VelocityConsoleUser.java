package be.dieterblancke.bungeeutilisalsx.velocity.user;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class VelocityConsoleUser extends ConsoleUser
{

    @Override
    public void sendMessage( Component component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }

        asAudience().sendMessage( component );
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

    @Override
    public Audience asAudience()
    {
        return Bootstrap.getInstance().getProxyServer().getConsoleCommandSource();
    }
}
