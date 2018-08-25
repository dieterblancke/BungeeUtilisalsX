package com.dbsoftwares.bungeeutilisals.utils.redis;

/*
 * Created by DBSoftwares on 26 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

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