package com.dbsoftwares.bungeeutilisalsx.spigot.api.utils;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

@Data
public class UuidInventoryHolder implements InventoryHolder
{

    private final UUID uuid;
    private Inventory inventory;

    public UuidInventoryHolder()
    {
        this( UUID.randomUUID() );
    }

    public UuidInventoryHolder( final UUID uuid )
    {
        this.uuid = uuid;
    }
}
