package com.dbsoftwares.bungeeutilisals.bungee.experimental.executors;

import com.dbsoftwares.bungeeutilisals.api.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.experimental.connection.BungeeConnection;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.OnPacketEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryRegistry;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.PlayerInventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInCloseWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInWindowClick;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OnPacketExecutor implements EventExecutor<OnPacketEvent> {

    @Override
    public void onExecute(OnPacketEvent event) {
        if (event.getSender() instanceof ServerConnection && event.getReciever() instanceof BungeeConnection) {
            if (event.getPacket() instanceof OutWindowItems) {
                OutWindowItems items = (OutWindowItems) event.getPacket();
                if (items.id == 0) {
                    PlayerInventory inv = InventoryRegistry.getPlayerInventory(event.getPlayer().getUniqueId());
                    inv.setItems(items.items);
                    InventoryRegistry.getPlayerInventories().put(event.getPlayer().getUniqueId(), inv);
                }
            }
            if (event.getPacket() instanceof OutSetSlot) {
                OutSetSlot slot = (OutSetSlot) event.getPacket();
                if (slot.windowID == 0) {
                    PlayerInventory inv = InventoryRegistry.getPlayerInventory(event.getPlayer().getUniqueId());
                    inv.setItem(slot.slot, slot.item);
                    InventoryRegistry.getPlayerInventories().put(event.getPlayer().getUniqueId(), inv);
                }
            }
        }

        if (event.getSender() instanceof ProxiedPlayer && event.getReciever() instanceof BungeeConnection) {
            if (event.getPacket() instanceof PacketPlayInCloseWindow) {
                PacketPlayInCloseWindow packet = (PacketPlayInCloseWindow) event.getPacket();
                if (InventoryRegistry.getOpen().containsKey(event.getPlayer().getUniqueId())) {
                    if (InventoryRegistry.getOpen().get(event.getPlayer().getUniqueId()) == packet.windowID) {
                        event.setCancelled(true);
                    }
                    InventoryRegistry.getOpen().remove(event.getPlayer().getUniqueId());
                }
            }

            if (event.getPacket() instanceof PacketPlayInWindowClick) {
                PacketPlayInWindowClick packet = (PacketPlayInWindowClick) event.getPacket();

                if (InventoryRegistry.getOpen().containsKey(event.getPlayer().getUniqueId())) {
                    if (InventoryRegistry.getOpen().get(event.getPlayer().getUniqueId()) == packet.windowID) {
                        event.setCancelled(true);

                        if (InventoryRegistry.getInventories().containsKey(packet.windowID)) {
                            Inventory i = InventoryRegistry.getInventories().get(packet.windowID);

                            try {
                                Method handleClick = ReflectionUtils.getMethod(Inventory.class, "handleClick", ProxiedPlayer.class,
                                        ItemStack.class, int.class, boolean.class, boolean.class);

                                handleClick.invoke(i, event.getPlayer(), packet.item, packet.slot, packet.shift == 1, packet.button == 0);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        InventoryRegistry.getOpen().remove(event.getPlayer().getUniqueId());
                    }
                }
            }
        }
    }
}