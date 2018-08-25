package com.dbsoftwares.bungeeutilisals.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;

import java.lang.reflect.Method;

public class APIHandler {

    private static final Method REGISTER;

    static {
        Method register = null;
        try {
            register = BUCore.class.getDeclaredMethod("initAPI", BUAPI.class);
            register.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        REGISTER = register;
    }

    public static void registerProvider(BUAPI api) {
        try {
            REGISTER.invoke(null, api);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}