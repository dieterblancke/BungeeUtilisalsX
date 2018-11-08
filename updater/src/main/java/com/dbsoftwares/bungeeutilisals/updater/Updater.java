/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.updater;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Sets;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
public class Updater {

    private static Set<Updater> updaters = Sets.newConcurrentHashSet();

    private Updatable updatable;

    public Updater(final Updatable updatable) {
        updaters.add(this);

        this.updatable = updatable;
        initialize();
    }

    private void initialize() {
        /*
            updater:
              # Set to true to enable updater checks.
              enabled: true
              # Set to -1 to disable repeating updater checks.
              delay: 30
              # Set to true to automatically install updates on restart.
              install: false
         */
        final IConfiguration config = BUCore.getApi().getConfig(FileLocation.CONFIG);
        final int delay = config.getInteger("updater.delay");

        // TODO

        ProxyServer.getInstance().getScheduler().schedule(BUCore.getApi().getPlugin(), () -> {
        }, 0, delay, TimeUnit.MINUTES);
    }

    public void onShutdown() {

    }

    private boolean shouldInstall() {
        return BUCore.getApi().getConfig(FileLocation.CONFIG).getBoolean("updater.install");
    }
}