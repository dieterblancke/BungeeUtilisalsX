package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

import lombok.Getter;

public enum InventoryType {

    CHEST_INVENTORY(1, "Chest");

    @Getter int id;
    @Getter String name;

    InventoryType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}