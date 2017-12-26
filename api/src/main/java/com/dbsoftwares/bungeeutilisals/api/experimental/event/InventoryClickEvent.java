package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InventoryClickEvent extends AbstractEvent implements Cancellable {

    Boolean cancelled = false;

    ProxiedPlayer p;
    int slot;
    Inventory inventory;
    ItemStack clickedItem;
    boolean leftClick;
    boolean shiftClick;

    public InventoryClickEvent(ProxiedPlayer p, Inventory inventory, int slot, ItemStack clicked, boolean lefltClick, boolean shiftClick) {
        this.p = p;
        this.inventory = inventory;
        this.slot = slot;
        this.clickedItem = clicked;
        this.leftClick = lefltClick;
        this.shiftClick = shiftClick;
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

    public boolean isLeftClick() {
        return leftClick;
    }

    public boolean isRightClick() {
        return !leftClick;
    }

    public boolean isShiftClick() {
        return shiftClick;
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
