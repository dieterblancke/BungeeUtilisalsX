package com.dbsoftwares.bungeeutilisals.bungee.executors;

import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.user.User;

public class UserExecutor {

    public void onLoad(UserLoadEvent event) {
        User user = event.getUser();
        event.getApi().getUsers().add(user);
    }

    public void onUnload(UserUnloadEvent event) {
        User user = event.getUser();
        event.getApi().getUsers().remove(user);
    }
}