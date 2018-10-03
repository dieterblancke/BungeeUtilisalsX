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

package com.dbsoftwares.bungeeutilisals.api.addon;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public abstract class Addon {

    private ProxyServer proxy;
    private BUAPI api;
    private AddonDescription description;
    private ExecutorService executorService;

    private void initialize(final ProxyServer proxy, final BUAPI api, final AddonDescription description) {
        this.proxy = proxy;
        this.api = api;
        this.description = description;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public ExecutorService getExecutorService() {
        if (this.executorService == null) {
            final String name = description == null ? "UNKNOWN" : description.getName();
            this.executorService = Executors.newCachedThreadPool(
                    new ThreadFactoryBuilder().setNameFormat(name + " Pool Thread #%1$d").build()
            );
        }

        return this.executorService;
    }

    public InputStream getResource(final String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    public File getDataFolder() {
        return new File(api.getAddonManager().getAddonsFolder(), getDescription().getName());
    }

    public IScheduler getScheduler() {
        return api.getAddonManager().getScheduler();
    }

    public void registerListener(final Listener listener) {
        api.getAddonManager().registerListener(this, listener);
    }

    public void registerEventHandler(final EventHandler handler) {
        api.getAddonManager().registerEventHandler(this, handler);
    }

    public void registerCommand(final Command command) {
        api.getAddonManager().registerCommand(this, command);
    }
}
