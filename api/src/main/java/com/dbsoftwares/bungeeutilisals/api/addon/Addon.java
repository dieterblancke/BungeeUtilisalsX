package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

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
