package dev.endoy.bungeeutilisalsx.webapi.commands;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.webapi.caching.CachingAspect;

import java.util.List;

public class CacheCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( !args.isEmpty() )
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
