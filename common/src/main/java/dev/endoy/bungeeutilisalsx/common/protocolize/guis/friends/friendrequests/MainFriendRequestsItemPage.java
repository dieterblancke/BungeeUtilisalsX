package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;

public class MainFriendRequestsItemPage extends ItemPage
{

    public MainFriendRequestsItemPage( final User user, final MainFriendRequestsGuiConfig guiConfig )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( user, item ) );
            }
        }
    }
}
