package dev.endoy.bungeeutilisalsx.common.api.friends;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.ISection;

public class FriendUtils
{

    private FriendUtils()
    {
    }

    public static int getFriendLimit( final User user )
    {
        final String permission = ConfigFiles.FRIENDS_CONFIG.getConfig().getString( "friendlimits.permission" );
        int highestLimit = 0;

        for ( ISection section : ConfigFiles.FRIENDS_CONFIG.getConfig().getSectionList( "friendlimits.limits" ) )
        {
            final String name = section.getString( "name" );
            final int limit = section.getInteger( "limit" );

            if ( !name.equalsIgnoreCase( "default" ) && !user.hasPermission( permission + name ) )
            {
                continue;
            }

            if ( limit > highestLimit )
            {
                highestLimit = limit;
            }
        }

        return highestLimit;
    }
}
