package com.dbsoftwares.bungeeutilisals.bungee.executors;

/*
 * Created by DBSoftwares on 12/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;

public class UserPunishExecutor implements EventExecutor {

    @Event
    public void updateMute(UserPunishEvent event) {
        if (event.isMute()) {
            event.getUser().ifPresent(user -> user.setMute(event.getInfo()));
        }
    }

    @Event
    public void executeActions(UserPunishEvent event) {
        if(!FileLocation.PUNISHMENTS_CONFIG.hasData(event.getType().toString())) {
            return;
        }
        List<PunishmentAction> actions = FileLocation.PUNISHMENTS_CONFIG.getData(event.getType().toString());

        for (PunishmentAction action : actions) {
            if (event.isUserPunishment()) {
                // UUID involved
            } else {
                // IP involved
            }
        }
    }
}