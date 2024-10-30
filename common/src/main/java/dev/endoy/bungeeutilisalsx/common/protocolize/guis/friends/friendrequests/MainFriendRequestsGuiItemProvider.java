package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

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
