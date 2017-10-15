package com.dbsoftwares.bungeeutilisals.api.event;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import lombok.Getter;
import lombok.Setter;

public class AbstractEvent implements BUEvent {

    @Getter @Setter BUAPI api;

}