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

import com.dbsoftwares.bungeeutilisals.commands.general.AnnounceCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.ChatLockCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.ClearChatCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.StaffChatCommand;
import com.google.gson.Gson;

public enum Channels {

    ANNOUNCE() {

        private Gson gson = new Gson();

        @Override
        public void execute(String message) {
            AnnounceCommand.AnnounceMessage announcement
                    = gson.fromJson(message, AnnounceCommand.AnnounceMessage.class);

            AnnounceCommand.sendAnnounce(announcement);
        }
    },
    CLEARCHAT() {

        private Gson gson = new Gson();

        @Override
        public void execute(String message) {
            ClearChatCommand.ClearData data = gson.fromJson(message, ClearChatCommand.ClearData.class);

            ClearChatCommand.clearChat(data.getServer(), data.getBy());
        }
    },
    CHATLOCK() {

        private Gson gson = new Gson();

        @Override
        public void execute(String message) {
            ChatLockCommand.LockData data = gson.fromJson(message, ChatLockCommand.LockData.class);

            ChatLockCommand.lockChat(data.getServer(), data.getBy());
        }
    },
    STAFFCHAT() {

        private Gson gson = new Gson();

        @Override
        public void execute(String message) {
            StaffChatCommand.StaffChatData data = gson.fromJson(message, StaffChatCommand.StaffChatData.class);

            StaffChatCommand.sendStaffChatMessage(data.getServer(), data.getPlayer(), data.getMessage());
        }
    };

    public abstract void execute(String message);

    @Override
    public String toString() {
        return "BUNGEEUTILISALS_" + super.toString();
    }
}