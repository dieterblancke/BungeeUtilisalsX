package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInWindowClick;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InventoryClickEvent extends AbstractEvent implements Cancellable {

    Boolean cancelled = false;

    ProxiedPlayer p;
    int slot;
    Inventory inventory;
    ItemStack clickedItem;
    int mode;
    int action;

    public InventoryClickEvent(ProxiedPlayer p, Inventory inventory, int slot, ItemStack clicked, int mode, int action) {
        this.p = p;
        this.inventory = inventory;
        this.slot = slot;
        this.clickedItem = clicked;
        this.mode = mode;
        this.action = action;
    }

    public ProxiedPlayer getPlayer() {
        return p;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isDrop() {
        return PacketPlayInWindowClick.isDrop(mode);
    }

    public boolean isDrag() {
        return PacketPlayInWindowClick.isDrag(mode);
    }

    public boolean isKeyPress() {
        return PacketPlayInWindowClick.isKey(mode);
    }

    public boolean isLeftClick() {
        int mode = this.mode >> 4;

        return mode == 0 && action == 0;
    }

    public boolean isRightClick() {
        int mode = this.mode >> 4;

        return mode == 0 && action == 1;
    }

    public boolean isScrollClick() {
        int mode = this.mode >> 4;

        return mode == 3;
    }

    public boolean isDoubleClick() {
        int mode = this.mode >> 4;

        return mode == 6;
    }

    public boolean isShiftClick() {
        return PacketPlayInWindowClick.isShiftClick(mode);
    }

    @Override
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Boolean isCancelled() {
        return cancelled;
    }
}
