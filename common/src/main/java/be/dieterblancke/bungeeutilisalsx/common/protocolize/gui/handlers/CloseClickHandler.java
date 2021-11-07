package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.handlers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TriConsumer;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.simplix.protocolize.api.inventory.InventoryClick;

public class CloseClickHandler implements TriConsumer<Gui, User, InventoryClick>
{

    @Override
    public void accept( final Gui gui, final User user, final InventoryClick event )
    {
        event.cancelled( true );

        BuX.getInstance().getProtocolizeManager().closeInventory( user );
    }
}
