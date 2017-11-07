package com.dbsoftwares.bungeeutilisals.bungee.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;

import java.lang.reflect.Method;

public class APIHandler {

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        Method register = null;
        Method unregister = null;
        try {
            register = BUCore.class.getDeclaredMethod("registerApi", BUAPI.class);
            register.setAccessible(true);

            unregister = BUCore.class.getDeclaredMethod("unregisterApi");
            unregister.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        REGISTER = register;
        UNREGISTER = unregister;
    }

    public static void registerProvider(BUAPI api) {
        try {
            REGISTER.invoke(null, api);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterProvider() {
        try {
            UNREGISTER.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}