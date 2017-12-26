package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryRegistry {

    @Getter private static HashMap<UUID, Integer> open = Maps.newHashMap();
    @Getter private static HashMap<Integer, Inventory> inventories = Maps.newHashMap();
    @Getter private static HashMap<UUID, PlayerInventory> playerInventories = Maps.newHashMap();
    @Getter private static AtomicInteger currentInventoryId = new AtomicInteger(0);


    public static Inventory getInventoryById(int id) {
        return inventories.getOrDefault(id, null);
    }

    public static Inventory getOpenInventory(UUID id) {
        return open.containsKey(id) ? getInventoryById(open.get(id)) : null;
    }

    public static PlayerInventory getPlayerInventory(UUID id) {
        if (playerInventories.containsKey(id)) {
            return playerInventories.get(id);
        }
        PlayerInventory inv = new PlayerInventory(id);
        playerInventories.put(id, inv);
        return inv;
    }
}