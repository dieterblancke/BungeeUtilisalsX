package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;

public enum Platform
{

    BUNGEECORD,
    VELOCITYPOWERED,
    SPIGOT,
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
