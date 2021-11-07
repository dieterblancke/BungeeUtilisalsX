package be.dieterblancke.bungeeutilisalsx.webapi.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.webapi.caching.CachingAspect;

import java.util.List;

public class CacheCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() > 0 )
        {
            final String action = args.get( 0 );

            if ( action.equals( "clear" ) )
            {
                CachingAspect.clearCache();
                BuX.getLogger().info( "Cache has been cleared" );
                return;
            }
        }

        BuX.getLogger().info( "Cache Command help reference:" );
        BuX.getLogger().info( "- cache clear" );
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public String getUsage()
    {
        return null;
    }
}
