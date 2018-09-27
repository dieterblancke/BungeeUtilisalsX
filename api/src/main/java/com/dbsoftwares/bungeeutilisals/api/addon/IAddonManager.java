package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.File;
import java.util.Collection;

public interface IAddonManager {

    File getAddonsFolder();

    void findAddons(final File folder);

    void loadAddons();

    void enableAddons();

    void disableAddons();

    void disableAddon(final String addonName);

    Addon getAddon(final String addonName);

    Collection<Addon> getAddons();

    IScheduler getScheduler();

    void registerListener(final Addon addon, final Listener listener);

    void registerEventHandler(final Addon addon, final EventHandler handler);

    void registerCommand(final Addon addon, final Command command);

    Collection<Listener> getListeners(final String addonName);

    Collection<EventHandler> getEventHandlers(final String addonName);

    Collection<Command> getCommands(final String addonName);

}
