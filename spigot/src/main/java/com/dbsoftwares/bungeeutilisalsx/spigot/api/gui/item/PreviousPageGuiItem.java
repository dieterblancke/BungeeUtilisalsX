package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item;

import org.bukkit.inventory.ItemStack;

public class PreviousPageGuiItem extends ClickableGuiItem
{

    public PreviousPageGuiItem( final ItemStack itemStack )
    {
        super( itemStack, ( gui, player, event ) ->
        {
            event.setCancelled( true );
            gui.setPage( gui.getPage() - 1 );
            gui.refill();
        } );
    }
}
