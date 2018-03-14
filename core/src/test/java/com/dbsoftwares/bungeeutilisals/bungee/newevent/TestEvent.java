package com.dbsoftwares.bungeeutilisals.bungee.newevent;

/*
 * Created by DBSoftwares on 14/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.event.event.BUEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

public class TestEvent implements BUEvent, Cancellable {

    @Getter
    @Setter
    String message;
    @Getter
    @Setter
    boolean cancelled = false;

    public TestEvent(String message) {
        this.message = message;
    }

    @Override
    public BUAPI getApi() {
        return null;
    }
}