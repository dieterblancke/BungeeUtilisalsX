package com.dbsoftwares.bungeeutilisalsx.spigot.listeners;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.utils.UuidInventoryHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class InventoryListener implements Listener
{

    @EventHandler
    public void onInventoryClick( final InventoryClickEvent event )
    {
        final Inventory inventory = event.getInventory();

        if ( inventory == null || inventory.getHolder() == null || !( inventory.getHolder() instanceof UuidInventoryHolder ) )
        {
            return;
        }
        final UuidInventoryHolder inventoryHolder = (UuidInventoryHolder) inventory.getHolder();
        final Optional<Gui> optionalGui = ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().findByUuid( inventoryHolder.getUuid() );

        optionalGui.ifPresent( gui -> gui.handleInventoryClick( event ) );
    }
}
