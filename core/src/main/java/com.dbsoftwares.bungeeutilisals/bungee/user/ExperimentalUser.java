package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.experimental.event.InventoryCloseEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.InventoryType;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutCloseWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutOpenWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutWindowItems;
import com.dbsoftwares.bungeeutilisals.api.user.IExperimentalUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExperimentalUser implements IExperimentalUser {

    private final BUser user;
    private Inventory currentInventory;

    @Override
    public void openInventory(Inventory inventory) {
        openInventory(inventory, true);
    }

    @Override
    public void openInventory(Inventory inv, Boolean resetCursor) {
        if(hasOpenInventory()) {
            closeInventory();
        }
        PacketPlayOutOpenWindow e = new PacketPlayOutOpenWindow(99, inv.getType().getMCName(), inv.getTitle(),
                inv.getType().equals(InventoryType.CHEST) ? inv.getSize() : inv.getType().getDefaultSlots(), false);

        this.sendPacket(e);
        this.sendPacket(new PacketPlayOutWindowItems(99, inv.getContents().values().toArray(new ItemStack[inv.getContents().size()])));

        inv.unsafe().getViewers().add(user);
        this.currentInventory = inv;
    }

    @Override
    public Boolean hasOpenInventory() {
        return currentInventory != null;
    }

    @Override
    public void closeInventory() {
        if(currentInventory == null) {
            return;
        }
        InventoryCloseEvent event = new InventoryCloseEvent(user.getParent(), currentInventory);
        if (event.isCancelled()) {
            return;
        }
        this.sendPacket(new PacketPlayOutCloseWindow(99));
        currentInventory.unsafe().getViewers().remove(user);
        currentInventory = null;
    }

    @Override
    public void sendPacket(Packet packet) {
        user.getParent().unsafe().sendPacket(packet);
    }

    public void unload() {
        if (hasOpenInventory()) {
            closeInventory();
        }
    }
}