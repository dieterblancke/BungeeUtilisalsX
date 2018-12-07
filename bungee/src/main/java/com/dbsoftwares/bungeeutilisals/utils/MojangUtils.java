/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.utils;

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
            BUCore.getLogger().warn("Could not retrieve uuid of " + name);
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
            BUCore.getLogger().warn("Could not retrieve name of " + uuid.toString());
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