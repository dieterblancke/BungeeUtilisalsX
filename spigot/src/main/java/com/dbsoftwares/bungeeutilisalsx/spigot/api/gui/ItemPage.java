package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui;

import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public class ItemPage
{

    private final GuiItem[] items;

    public ItemPage( final int slots )
    {
        items = new GuiItem[slots];
    }

    public void setRange( final GuiItem item, final int start, final int end )
    {
        this.setRange( item, start, end, false );
    }

    public void setRange( final GuiItem item, final int start, final int end, final boolean clone )
    {
        for ( int i = start; i < end; i++ )
        {
            this.setItem( i, clone ? item.copy() : item );
        }
    }

    public void setItem( final int slot, final GuiItem item )
    {
        items[slot] = item;
    }

    public void removeItem( final int slot )
    {
        this.setItem( slot, null );
    }

    public Optional<GuiItem> getItem( final int slot )
    {
        if ( slot >= items.length )
        {
            return Optional.empty();
        }
        return Optional.of( items[slot] );
    }

    public void populateTo( final Inventory inventory )
    {
        for ( int i = 0; i < items.length; i++ )
        {
            inventory.setItem( i, items[i] == null ? null : items[i].asItemStack() );
        }
    }
}
