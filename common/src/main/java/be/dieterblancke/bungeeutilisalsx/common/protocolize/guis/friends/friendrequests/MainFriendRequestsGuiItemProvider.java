package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.Optional;

public class MainFriendRequestsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public MainFriendRequestsGuiItemProvider( final User user, final MainFriendRequestsGuiConfig config )
    {
        this.page = new MainFriendRequestsItemPage( user, config );
    }

    @Override
    public Optional<GuiItem> getItemAtSlot( final int page, final int rawSlot )
    {
        return this.getItemContents( page ).getItem( rawSlot );
    }

    @Override
    public ItemPage getItemContents( int page )
    {
        return this.page;
    }

    @Override
    public int getPageAmount()
    {
        return 1;
    }
}
