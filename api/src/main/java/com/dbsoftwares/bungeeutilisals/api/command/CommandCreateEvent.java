package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandCreateEvent extends AbstractEvent {

    @Getter
    public final BUCommand command;

}