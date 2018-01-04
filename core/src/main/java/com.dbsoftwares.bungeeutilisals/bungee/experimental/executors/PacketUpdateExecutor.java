package com.dbsoftwares.bungeeutilisals.bungee.experimental.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketSendEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketUpdateExecutor {

    public void onPacketUpdate(PacketUpdateEvent event) {
        if (event.getSender() instanceof ServerConnection && event.getReciever() instanceof BungeeConnection) {
            PacketSendEvent packetEvent = new PacketSendEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReciever());

            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (event.getSender() instanceof ProxiedPlayer && event.getReciever() instanceof BungeeConnection) {
            PacketReceiveEvent packetEvent = new PacketReceiveEvent(event.getPacket(), event.getPlayer(), event.getSender(), event.getReciever());

            BUCore.getApi().getEventLoader().launchEvent(packetEvent);

            if (packetEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    public void onPacketReceive(PacketReceiveEvent event) {

    }
}