package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.handlers;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.TriConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CloseClickHandler implements TriConsumer<Gui, Player, InventoryClickEvent>
{

    @Override
    public void accept( final Gui gui, final Player player, final InventoryClickEvent event )
    {
        event.setCancelled( true );
        player.closeInventory();
    }
}
