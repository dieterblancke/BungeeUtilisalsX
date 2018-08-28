package com.dbsoftwares.bungeeutilisals.utils;

/*
 * Created by DBSoftwares on 27/08/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.gson.Gson;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class MojangUtils {

    //private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    //private static final String NAME_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final String UUID_URL = "https://ss.gameapis.net/uuid/";
    private static final String NAME_URL = "https://ss.gameapis.net/name/";
    private static final Gson gson = new Gson();

    public static String getUUID(final String name) {
        try {
            final URL url = new URL(UUID_URL + name);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "BungeeUtilisals/" + BungeeUtilisals.getInstance().getDescription().getVersion());

            try (final InputStream input = conn.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(input)) {
                final MojangProfile profile = gson.fromJson(isr, MojangProfile.class);

                if (profile != null && !profile.uuid_formatted.isEmpty()) {
                    return profile.uuid_formatted;
                }
            }
        } catch (final IOException e) {
            BUCore.log(Level.WARNING, "Could not retrieve uuid of " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static String getName(final UUID uuid) {
        try {
            final URL url = new URL(NAME_URL + uuid.toString().replace("-", ""));
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("User-Agent", "BungeeUtilisals/" + BungeeUtilisals.getInstance().getDescription().getVersion());

            try (final InputStream input = conn.getInputStream();
                 final InputStreamReader isr = new InputStreamReader(input)) {
                final MojangProfile profile = gson.fromJson(isr, MojangProfile.class);

                if (profile != null && !profile.name.isEmpty()) {
                    return profile.name;
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            BUCore.log(Level.WARNING, "Could not retrieve name of " + uuid.toString());
        }
        return null;
    }

    @Data
    private class MojangProfile {

        private String id;
        private String name;
        private String uuid_formatted;

    }
}