package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item;

import org.bukkit.inventory.ItemStack;

public class ClosingGuiItem extends ClickableGuiItem
{

    public ClosingGuiItem( final ItemStack itemStack )
    {
        super( itemStack, ( gui, player, event ) ->
        {
            event.setCancelled( true );
            player.closeInventory();
        } );
    }
}
