package com.dbsoftwares.bungeeutilisals.utils;

/*
 * Created by DBSoftwares on 27/08/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.gson.Gson;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.logging.Level;

public class MojangUtils {

    //private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    //private static final String NAME_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static final String UUID_URL = "https://use.gameapis.net/mc/player/uuid/";
    private static final String NAME_URL = "https://use.gameapis.net/mc/player/name/";
    private static final Gson gson = new Gson();

    public static String getUUID(final String name) {
        try {
            final URL url = new URL(UUID_URL + name);
            final URLConnection conn = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                 BufferedReader reader = new BufferedReader(isr)) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }

                MojangProfile profile = gson.fromJson(content.toString(), MojangProfile.class);
                if (profile != null && !profile.id.isEmpty()) {
                    return profile.id;
                }
            }
        } catch (IOException e) {
            BUCore.log(Level.WARNING, "Could not retrieve uuid of " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static String getName(final UUID uuid) {
        try {
            final URL url = new URL(NAME_URL + uuid.toString().replace("-", ""));
            final URLConnection conn = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                 BufferedReader reader = new BufferedReader(isr)) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                System.out.println(content.toString());

                MojangProfile profile = gson.fromJson(content.toString(), MojangProfile.class);
                if (profile != null && !profile.name.isEmpty()) {
                    return profile.name;
                }
            }
        } catch (IOException e) {
            BUCore.log(Level.WARNING, "Could not retrieve name of " + uuid.toString());
            e.printStackTrace();
        }
        return null;
    }

    @Data
    private class MojangProfile {

        private String id;
        private String name;

    }
}