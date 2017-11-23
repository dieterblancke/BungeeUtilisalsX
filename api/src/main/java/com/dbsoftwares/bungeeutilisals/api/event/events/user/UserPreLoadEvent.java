package com.dbsoftwares.bungeeutilisals.api.event.events.user;

/*
 * Created by DBSoftwares on 26 september 2017
 * Developer: Dieter Blancke
 * Project: centrixcore
 */

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetAddress;

/**
 * Event which get's called right before an User get's loaded in.
 */
@RequiredArgsConstructor
public class UserPreLoadEvent extends AbstractEvent implements Cancellable {

    private Boolean cancelled = false;
    @Getter private final ProxiedPlayer player;
    @Getter private final InetAddress address;

    @Override
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Boolean isCancelled() {
        return cancelled;
    }
}