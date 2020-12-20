package be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item;

import org.bukkit.inventory.ItemStack;

public class CancelledGuiItem extends ClickableGuiItem
{
    public CancelledGuiItem( final ItemStack itemStack )
    {
        super( itemStack, ( gui, player, event ) -> event.setCancelled( true ) );
    }
}
