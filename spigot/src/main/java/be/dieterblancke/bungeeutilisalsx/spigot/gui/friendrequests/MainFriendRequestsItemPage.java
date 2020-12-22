package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;

public class MainFriendRequestsItemPage extends ItemPage
{

    public MainFriendRequestsItemPage( final MainFriendRequestsGuiConfig guiConfig )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( item ) );
            }
        }
    }
}
