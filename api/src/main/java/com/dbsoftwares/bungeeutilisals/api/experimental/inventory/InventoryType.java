package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

import lombok.Getter;

public enum InventoryType {

    CHEST(0, "Chest", "minecraft:chest", 27),
    WORKBENCH(1, "Workbench", "minecraft:crafting_table", 10),
    FURNACE(2, "Furnace", "minecraft:furnace", 3),
    DISPENSER(3, "Dispenser", "minecraft:dispenser", 9),
    ENCHANTMENT_TABLE(4, "Enchantment Table", "minecraft:enchanting_table", 2),
    BREWING_STAND(5, "Brewing Stand", "minecraft:brewing_stand", 4),
    ANVIL(6, "Anvil", "minecraft:anvil", 3),
    HOPPER(7, "Hopper", "minecraft:hopper", 5),
    DROPPER(8, "Dropper", "minecraft:dropper", 9);

    @Getter int id;
    @Getter String name;
    @Getter String MCName;
    @Getter int defaultSlots;

    InventoryType(int id, String name, String MCName, int defaultSlots) {
        this.id = id;
        this.name = name;
        this.MCName = MCName;
        this.defaultSlots = defaultSlots;
    }
}