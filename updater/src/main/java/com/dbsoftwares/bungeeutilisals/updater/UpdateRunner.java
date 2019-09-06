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

package com.dbsoftwares.bungeeutilisals.updater;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class UpdateRunner implements Runnable {

    private static final Gson gson;
    private static final HttpRequestFactory requestFactory;
    private static final String ERROR_STRING = "An error occured: ";

    static {
        gson = new Gson();
        requestFactory = new NetHttpTransport().createRequestFactory();
    }

    private final Updater updater;

    private boolean updateFound;
    private GenericUrl url;

    @Override
    public void run() {
        final UpdatableData data = updater.getUpdatable();

        try {
            final HttpRequest request = requestFactory.buildGetRequest(data.getUrl());
            final HttpResponse response = request.executeAsync().get();

            if (response.isSuccessStatusCode()) {
                try (InputStream input = response.getContent();
                     InputStreamReader reader = new InputStreamReader(input)) {
                    final JsonObject object = gson.fromJson(reader, JsonObject.class);

                    final String status = object.get("status").getAsString();
                    if (status.equalsIgnoreCase("success")) {
                        final String version = object.get("version").getAsString();

                        if (checkForUpdate(data.getCurrentVersion(), version)) {
                            // update found
                            updateFound = true;
                            url = new GenericUrl(object.get("downloadurl").getAsString());

                            BUCore.getApi().langPermissionBroadcast(
                                    "updater.update-found",
                                    BUCore.getApi().getConfig(FileLocation.CONFIG).getString("updater.permission"),
                                    "{name}", data.getName(),
                                    "{version}", data.getCurrentVersion(),
                                    "{newVersion}", version
                            );
                        }
                    }
                }
            }
        } catch (IOException | ExecutionException e) {
            BUCore.getLogger().error(ERROR_STRING +  e.getMessage());
        } catch (InterruptedException e) {
            BUCore.getLogger().error(ERROR_STRING + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    protected void shutdown() {
        if (updateFound && url != null) {
            try {
                final HttpRequest request = requestFactory.buildGetRequest(url);
                final HttpResponse response = request.executeAsync().get();

                if (response.isSuccessStatusCode()) {
                    try (FileOutputStream fos = new FileOutputStream(updater.getUpdatable().getFile())) {
                        response.download(fos);
                    }
                }
            } catch (IOException | ExecutionException e) {
                BUCore.getLogger().error(ERROR_STRING + e.getMessage());
            } catch (InterruptedException e) {
                BUCore.getLogger().error(ERROR_STRING + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean checkForUpdate(final String version, final String newVersion) {
        if (MathUtils.isInteger(version) && MathUtils.isInteger(newVersion)) {
            return Integer.parseInt(version) < Integer.parseInt(newVersion);
        }
        try {
            return checkStringVersion(version, newVersion);
        } catch (Exception e) {
            // if for some reason an exception pops up, it will default back to a simple equals check
            return !newVersion.equals(version);
        }
    }

    private boolean checkStringVersion(String version, String newVersion) {
        version = version.replaceAll("[^\\d]", "");
        newVersion = newVersion.replaceAll("[^\\d]", "");

        return Integer.parseInt(newVersion) > Integer.parseInt(version);
    }
}
