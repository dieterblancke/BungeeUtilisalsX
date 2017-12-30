package com.dbsoftwares.bungeeutilisals.bungee.experimental.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.*;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInCloseWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInWindowClick;
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
        if (event.getPacket() instanceof PacketPlayInWindowClick) {
            PacketPlayInWindowClick packet = (PacketPlayInWindowClick) event.getPacket();

            if (packet.getWindow() != 99) {
                return;
            }
            Inventory inventory = event.getUser().experimental().getOpenInventory();
            InventoryClickEvent clickEvent = new InventoryClickEvent(event.getPlayer(), inventory, packet.slot, packet.item, packet.mode, packet.actionNumber);
            BUCore.getApi().getEventLoader().launchEvent(clickEvent);

            if (clickEvent.isCancelled()) {
                // TODO: Cancellation of event.

            }
        } else if (event.getPacket() instanceof PacketPlayInCloseWindow) {
            PacketPlayInCloseWindow packet = (PacketPlayInCloseWindow) event.getPacket();

            if (packet.getWindow() != 99) {
                return;
            }

            Inventory inventory = event.getUser().experimental().getOpenInventory();
            InventoryCloseEvent closeEvent = new InventoryCloseEvent(event.getPlayer(), inventory);
            BUCore.getApi().getEventLoader().launchEvent(closeEvent);
        }
    }
}