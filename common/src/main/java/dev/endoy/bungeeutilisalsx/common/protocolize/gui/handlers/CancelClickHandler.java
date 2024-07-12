package dev.endoy.bungeeutilisalsx.common.protocolize.gui.handlers;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.TriConsumer;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.simplix.protocolize.api.inventory.InventoryClick;

public class CancelClickHandler implements TriConsumer<Gui, User, InventoryClick>
{

    @Override
    public void accept( final Gui gui, final User user, final InventoryClick event )
    {
        event.cancelled( true );
    }
}
