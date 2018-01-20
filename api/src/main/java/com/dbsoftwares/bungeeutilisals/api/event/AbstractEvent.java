package com.dbsoftwares.bungeeutilisals.api.event;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.BUEvent;
import lombok.Getter;
import lombok.Setter;

public class AbstractEvent implements BUEvent {

    @Getter @Setter BUAPI api;

}