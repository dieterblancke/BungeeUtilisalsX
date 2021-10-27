package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.Optional;

public interface PageableItemProvider
{

    default Optional<GuiItem> getItemAtSlot( final int rawSlot )
    {
        return getItemAtSlot( 1, rawSlot );
    }

    default ItemPage getItemContents()
    {
        return getItemContents( 1 );
    }

    Optional<GuiItem> getItemAtSlot( int page, int rawSlot );

    ItemPage getItemContents( int page );

    int getPageAmount();
}
