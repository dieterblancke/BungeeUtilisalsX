package com.dbsoftwares.bungeeutilisals.bungee.executors;

/*
 * Created by DBSoftwares on 12/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.EventExecutor;

public class UserPunishExecutor implements EventExecutor<UserPunishEvent> {

    @Override
    public void onExecute(UserPunishEvent event) {
        if (event.isMute()) {
            event.getUser().ifPresent(user -> user.setMute(event.getInfo()));
        }
    }
}