package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item;

import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface GuiItem
{

    GuiItem copy();

    ItemStack asItemStack();

    void onClick( final Gui gui, final Player player, final InventoryClickEvent event );

}
