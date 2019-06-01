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

package com.dbsoftwares.bungeeutilisals.api.addon;

import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface IAddonManager {

    File getAddonsFolder();

    void findAddons(final File folder);

    void loadAddons();

    void loadSingleAddon(final File addonFile);

    void enableAddons();

    void enableAddon(final String addonName);

    void disableAddons();

    void disableAddon(final String addonName);

    void reloadAddon(final String addonName);

    Addon getAddon(final String addonName);

    boolean isRegistered(final String addonName);

    Collection<Addon> getAddons();

    IScheduler getScheduler();

    void registerListener(final Addon addon, final Listener listener);

    void registerEventHandler(final Addon addon, final EventHandler handler);

    void registerCommand(final Addon addon, final Command command);

    Collection<Listener> getListeners(final String addonName);

    Collection<EventHandler> getEventHandlers(final String addonName);

    Collection<Command> getCommands(final String addonName);

    ILanguageManager getLanguageManager();

    List<AddonData> getAllAddons();
}
