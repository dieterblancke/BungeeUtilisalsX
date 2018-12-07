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

package com.dbsoftwares.bungeeutilisals.utils.redis;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.commands.general.AnnounceCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.ChatLockCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.ClearChatCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.StaffChatCommand;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.APIAnnouncement;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.AnnounceMessage;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.ChatActionData;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.StaffChatData;
import com.google.gson.Gson;

import java.util.stream.Stream;

public enum Channels {

    API_BROADCAST() {

        private final Gson gson = new Gson();

        @Override
        public void execute(final String message) {
            final APIAnnouncement announcement = gson.fromJson(message, APIAnnouncement.class);
            Stream<User> users = BUCore.getApi().getUsers().stream();

            if (announcement.getPermission() != null) {
                users = users.filter(user -> user.getParent().hasPermission(announcement.getPermission()));
            }

            if (announcement.isLanguage()) {
                ILanguageManager languageManager = announcement.isPluginLanguageManager() ? BUCore.getApi().getLanguageManager() : BUCore.getApi().getAddonManager().getLanguageManager();
                users.forEach(user -> LanguageUtils.sendLangMessage(languageManager, announcement.getPlugin(), user, announcement.getMessage(), announcement.getPlaceHolders()));
            } else {
                if (announcement.getPrefix() == null) {
                    users.forEach(user -> user.sendMessage(announcement.getMessage()));
                } else {
                    users.forEach(user -> user.sendMessage(announcement.getPrefix(), announcement.getMessage()));
                }
            }
        }
    },
    ANNOUNCE() {

        private final Gson gson = new Gson();

        @Override
        public void execute(final String message) {
            final AnnounceMessage announcement = gson.fromJson(message, AnnounceMessage.class);

            AnnounceCommand.sendAnnounce(announcement);
        }
    },
    CLEARCHAT() {

        private final Gson gson = new Gson();

        @Override
        public void execute(final String message) {
            final ChatActionData data = gson.fromJson(message, ChatActionData.class);

            ClearChatCommand.clearChat(data.getServer(), data.getBy());
        }
    },
    CHATLOCK() {

        private final Gson gson = new Gson();

        @Override
        public void execute(final String message) {
            final ChatActionData data = gson.fromJson(message, ChatActionData.class);

            ChatLockCommand.lockChat(data.getServer(), data.getBy());
        }
    },
    STAFFCHAT() {

        private final Gson gson = new Gson();

        @Override
        public void execute(final String message) {
            final StaffChatData data = gson.fromJson(message, StaffChatData.class);

            StaffChatCommand.sendStaffChatMessage(data.getServer(), data.getPlayer(), data.getMessage());
        }
    };

    public abstract void execute(final String message);

    @Override
    public String toString() {
        return "BUNGEEUTILISALS_" + super.toString();
    }

}