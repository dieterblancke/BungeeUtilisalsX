package com.dbsoftwares.bungeeutilisals.utils.redis;

/*
 * Created by DBSoftwares on 26 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.commands.general.AnnounceCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.ClearChatCommand;
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
    };

    public abstract void execute(String message);

    @Override
    public String toString() {
        return "BUNGEEUTILISALS_" + super.toString();
    }
}