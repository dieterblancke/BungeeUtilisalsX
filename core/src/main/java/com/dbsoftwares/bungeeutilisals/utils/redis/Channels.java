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
        @Override
        public void execute(String message) {
            AnnounceCommand.AnnounceMessage announcement
                    = new Gson().fromJson(message, AnnounceCommand.AnnounceMessage.class);

            AnnounceCommand.sendAnnounce(announcement);
        }
    },
    CLEARCHAT() {
        @Override
        public void execute(String message) {
            ClearChatCommand.clearChat(message);
        }
    };

    public abstract void execute(String message);

    @Override
    public String toString() {
        return "BUNGEEUTILISALS_" + super.toString();
    }
}