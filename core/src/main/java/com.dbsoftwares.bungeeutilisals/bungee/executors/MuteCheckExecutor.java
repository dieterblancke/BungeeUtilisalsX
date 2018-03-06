package com.dbsoftwares.bungeeutilisals.bungee.executors;

/*
 * Created by DBSoftwares on 15/02/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

public class MuteCheckExecutor {

    public void onCommand(UserCommandEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();

        if (BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getStringList("blocked-mute-commands")
                .contains(event.getActualCommand().replaceFirst("/", ""))) {

            user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                    BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands." + info.getType().toString().toLowerCase() + ".onmute"),
                    event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
            event.setCancelled(true);
        }
    }

    public void onChat(UserChatEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();

        user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands." + info.getType().toString().toLowerCase() + ".onmute"),
                event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
        event.setCancelled(true);
    }
}