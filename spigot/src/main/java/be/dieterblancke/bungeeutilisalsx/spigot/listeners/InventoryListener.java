package be.dieterblancke.bungeeutilisalsx.spigot.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.utils.UuidInventoryHolder;
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

        if ( inventory == null || inventory.getHolder() == null || !( inventory.getHolder() instanceof final UuidInventoryHolder inventoryHolder ) )
        {
            return;
        }
        final Optional<Gui> optionalGui = ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().findByUuid( inventoryHolder.getUuid() );

        optionalGui.ifPresent( gui -> gui.handleInventoryClick( event ) );
    }
}
