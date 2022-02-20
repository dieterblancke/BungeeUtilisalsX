package be.dieterblancke.bungeeutilisalsx.common.api.friends;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.configuration.api.IConfiguration;

import java.util.Arrays;

public enum FriendSetting
{

    REQUESTS,
    MESSAGES,
    SERVER_SWITCH,
    FRIEND_BROADCAST;

    public static FriendSetting[] getEnabledSettings()
    {
        return Arrays.stream( values() )
                .filter( setting -> ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "settings." + setting.toString().toLowerCase() ) )
                .toArray( FriendSetting[]::new );
    }

    public String getName()
    {
        return toString().charAt( 0 ) + toString().substring( 1 ).toLowerCase();
    }

    public String getName( final IConfiguration language )
    {
        return language.exists( "friends.settings.type." + toString().toLowerCase() )
                ? language.getString( "friends.settings.type." + toString().toLowerCase() )
                : getName();
    }

    public boolean getDefault()
    {
        return ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "settings." + toString().toLowerCase() );
    }
}
