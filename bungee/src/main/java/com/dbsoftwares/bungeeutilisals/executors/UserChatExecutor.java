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
import com.dbsoftwares.bungeeutilisals.api.chat.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;

public class UserChatExecutor implements EventExecutor {

    private IChatManager manager;

    public UserChatExecutor(IChatManager manager) {
        this.manager = manager;
    }

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
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = FileLocation.ANTISWEAR.getConfiguration();

        if (manager.checkForSwear(event.getUser(), message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(manager.replaceSwearWords(user, message, config.getString("replace")));
            }
            user.sendLangMessage("chat-protection.swear");
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onCapsChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();
        IConfiguration config = FileLocation.ANTICAPS.getConfiguration();

        if (manager.checkForCaps(user, message)) {
            if (config.getBoolean("cancel")) {
                event.setCancelled(true);
            } else {
                event.setMessage(event.getMessage().toLowerCase());
            }
            user.sendLangMessage("chat-protection.caps");
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onSpamChat(UserChatEvent event) {
        User user = event.getUser();

        if (manager.checkForSpam(user)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.spam", "%time%", user.getCooldowns().getLeftTime("CHATSPAM") / 1000);
        }
    }

    @Event(priority = Priority.HIGH, executeIfCancelled = false)
    public void onAdChat(UserChatEvent event) {
        User user = event.getUser();
        String message = event.getMessage();

        if (manager.checkForAdvertisement(user, message)) {
            event.setCancelled(true);

            user.sendLangMessage("chat-protection.advertise");
        }
    }
}