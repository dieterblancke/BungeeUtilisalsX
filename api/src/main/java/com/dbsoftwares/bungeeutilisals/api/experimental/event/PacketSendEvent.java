/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

public class PacketSendEvent extends AbstractEvent implements Cancellable {

    @Getter
    @Setter
    private boolean cancelled;
    private DefinedPacket packet;
    private Connection sender;
    private Connection reciever;
    private ProxiedPlayer player;

    public PacketSendEvent(DefinedPacket packet, ProxiedPlayer p, Connection sender, Connection reciever) {
        this.cancelled = false;
        this.player = p;
        this.packet = packet;
        this.sender = sender;
        this.reciever = reciever;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public User getUser() {
        return BUCore.getApi().getUser(player).orElse(null);
    }

    public DefinedPacket getPacket() {
        return packet;
    }

    public Connection getSender() {
        return sender;
    }

    public Connection getReciever() {
        return reciever;
    }
}