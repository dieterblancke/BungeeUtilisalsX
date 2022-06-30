package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;

public enum Platform
{

    BUNGEECORD,
    VELOCITYPOWERED,
    SPRING;

    private static Platform CURRENT_PLATFORM = null;

    public static Platform getCurrentPlatform()
    {
        if ( CURRENT_PLATFORM == null && BuX.getInstance() != null )
        {
            CURRENT_PLATFORM = BuX.getInstance().getPlatform();
        }

        return CURRENT_PLATFORM;
    }

    public static void setCurrentPlatform( Platform currentPlatform )
    {
        CURRENT_PLATFORM = currentPlatform;
    }
}
