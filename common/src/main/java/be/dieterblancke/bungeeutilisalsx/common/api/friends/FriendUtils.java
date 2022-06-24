package be.dieterblancke.bungeeutilisalsx.common.api.friends;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.configuration.api.ISection;

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
