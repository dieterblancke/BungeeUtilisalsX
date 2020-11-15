package com.dbsoftwares.bungeeutilisalsx.common;

import com.google.gson.Gson;

public class BuX
{
    private static final Gson GSON = new Gson();

    public static <T extends AbstractBungeeUtilisalsX> T getInstance()
    {
        return (T) AbstractBungeeUtilisalsX.getInstance();
    }

    public static IBuXApi getApi()
    {
        return getInstance().getApi();
    }

    public static Gson getGson()
    {
        return GSON;
    }
}
