package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.handlers;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TriConsumer;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.simplix.protocolize.api.inventory.InventoryClick;

public class PreviousPageClickHandler implements TriConsumer<Gui, User, InventoryClick>
{

    @Override
    public void accept( final Gui gui, final User user, final InventoryClick event )
    {
        event.cancelled( true );
        gui.setPage( gui.getPage() - 1 );
        gui.refill();
    }
}
