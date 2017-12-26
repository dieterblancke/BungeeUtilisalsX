package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InventoryCloseEvent extends AbstractEvent implements Cancellable {

    Boolean cancelled = false;

    ProxiedPlayer p;
    Inventory inventory;

    public InventoryCloseEvent(ProxiedPlayer p, Inventory inventory) {
        this.p = p;
        this.inventory = inventory;
    }

    public ProxiedPlayer getPlayer() {
        return p;
    }

    public Inventory getInventory() {
        return inventory;
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
