package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.ItemStack;

public interface GuiItem
{

    GuiItem copy();

    ItemStack asItemStack();

    void onClick( final Gui gui, final User user, final InventoryClick event );

}
