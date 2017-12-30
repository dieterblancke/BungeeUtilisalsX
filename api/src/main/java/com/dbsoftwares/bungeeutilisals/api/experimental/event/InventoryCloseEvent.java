package com.dbsoftwares.bungeeutilisals.api.experimental.event;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InventoryCloseEvent extends AbstractEvent {

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
}
