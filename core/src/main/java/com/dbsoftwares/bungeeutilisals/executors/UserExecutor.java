package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

public class UserExecutor implements EventExecutor {

    @Event
    public void onLoad(UserLoadEvent event) {
        User user = event.getUser();
        event.getApi().getUsers().add(user);
    }

    @Event
    public void onUnload(UserUnloadEvent event) {
        User user = event.getUser();
        event.getApi().getUsers().remove(user);
    }
}