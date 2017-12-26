package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
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
    public void openInventory(Inventory inventory, Boolean resetCursor) {
        if(hasOpenInventory()) {
            closeInventory(resetCursor);
        }
        PacketPlayOutOpenWindow e = new PacketPlayOutOpenWindow(Inventory.ID, inv.getType().getType(this.getVersion()), inv.getName(), inv.getType() == InventoryType.Chest ? inv.getSlots() : inv.getType().getDefaultSlots(), false);
        e.UTF_8 = true;
        this.sendPacket(e);
        this.sendPacket(new PacketPlayOutWindowItems(Inventory.ID, inv.getContains()));
        inv.unsave().getModificableViewerList().add(this);
        this.currentInventory = inv;
    }

    @Override
    public Boolean hasOpenInventory() {
        return null;
    }

    @Override
    public void closeInventory() {

    }

    @Override
    public void closeInventory(Boolean resetCursor) {

    }
}