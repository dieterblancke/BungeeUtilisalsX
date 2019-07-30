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

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.ProxyServer;

public class UserChatExecutor implements EventExecutor {

    @Event(priority = Priority.HIGHEST, executeIfCancelled = false)
    public void onUnicodeReplace(UserChatEvent event) {
        String message = event.getMessage();
        IConfiguration config = FileLocation.UTFSYMBOLS.getConfiguration();

        if (config.getBoolean("fancychat.enabled")
                && event.getUser().getParent().hasPermission(config.getString("fancychat.permission"))) {
            event.setMessage(BungeeUtilisals.getApi().getChatManager().fancyFont(message));
        }
    }

    @Event(priority = Priority.LOW, executeIfCancelled = false)
    public void onUnicodeSymbol(UserChatEvent event) {
        IConfiguration config = FileLocation.UTFSYMBOLS.getConfiguration();

        if (config.getBoolean("symbols.enabled") && event.getUser().getParent().hasPermission(config.getString("symbols.permission"))) {
            event.setMessage(event.getApi().getChatManager().replaceSymbols(event.getMessage()));
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onSwearChat(UserChatEvent event) {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = FileLocation.ANTISWEAR.getConfiguration();

        if (BUCore.getApi().getChatManager().checkForSwear(event.getUser(), message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(BUCore.getApi().getChatManager().replaceSwearWords(user, message, config.getString("replace")));
            }
            user.sendLangMessage("chat-protection.swear");

            if (config.exists("commands")) {
                config.getStringList("commands").forEach(command -> {
                    command = PlaceHolderAPI.formatMessage(user, command);

                    ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            command
                    );
                });
            }
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onCapsChat(UserChatEvent event) {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = FileLocation.ANTICAPS.getConfiguration();

        if (BUCore.getApi().getChatManager().checkForCaps(user, message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(event.getMessage().toLowerCase());
            }
            user.sendLangMessage("chat-protection.caps");

            if (config.exists("commands")) {
                config.getStringList("commands").forEach(command -> {
                    command = PlaceHolderAPI.formatMessage(user, command);

                    ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            command
                    );
                });
            }
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onSpamChat(UserChatEvent event) {
        final User user = event.getUser();
        final IConfiguration config = FileLocation.ANTISPAM.getConfiguration();

        if (BUCore.getApi().getChatManager().checkForSpam(user)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.spam", "%time%", user.getCooldowns().getLeftTime("CHATSPAM") / 1000);

            if (config.exists("commands")) {
                config.getStringList("commands").forEach(command -> {
                    command = PlaceHolderAPI.formatMessage(user, command);

                    ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            command
                    );
                });
            }
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onAdChat(UserChatEvent event) {
        final User user = event.getUser();
        final String message = event.getMessage();
        final IConfiguration config = FileLocation.ANTIAD.getConfiguration();

        if (BUCore.getApi().getChatManager().checkForAdvertisement(user, message)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.advertise");

            if (config.exists("commands")) {
                config.getStringList("commands").forEach(command -> {
                    command = PlaceHolderAPI.formatMessage(user, command);

                    ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            command
                    );
                });
            }
        }
    }
}