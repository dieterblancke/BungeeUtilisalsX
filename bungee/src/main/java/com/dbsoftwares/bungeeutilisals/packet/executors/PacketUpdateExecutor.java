/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.packet.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserMoveEvent;
import com.dbsoftwares.bungeeutilisals.api.user.Location;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.packet.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketSendEvent;
import com.dbsoftwares.bungeeutilisals.packet.event.PacketUpdateEvent;
import com.dbsoftwares.bungeeutilisals.packet.packets.position.PacketPlayInPlayerPosition;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketUpdateExecutor implements EventExecutor {

    private static final Class<?> ServerConnection = ReflectionUtils.getClass("net.md_5.bungee.ServerConnection");

    @Event
    public void onPacketUpdate(PacketUpdateEvent event) {
        if (ServerConnection.isInstance(event.getSender()) && event.getReceiver() instanceof BungeeConnection) {
            final PacketSendEvent packetEvent = new PacketSendEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReceiver());
            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (event.getSender() instanceof ProxiedPlayer && event.getReceiver() instanceof BungeeConnection) {
            final PacketReceiveEvent packetEvent = new PacketReceiveEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReceiver());
            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @Event
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof PacketPlayInPlayerPosition) {
            final User user = event.getUser();
            final PacketPlayInPlayerPosition packet = (PacketPlayInPlayerPosition) event.getPacket();
            final Location location = new Location(packet.getX(), packet.getY(), packet.getZ(), packet.isGround());

            final UserMoveEvent userMoveEvent = new UserMoveEvent(user, user.getLocation() == null ? location : user.getLocation(), location);
            BUCore.getApi().getEventLoader().launchEvent(userMoveEvent);

            user.setLocation(location);
        }
    }
}