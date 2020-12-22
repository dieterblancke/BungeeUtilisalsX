package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests;

import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.PageableItemProvider;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;

import java.util.Optional;

public class MainFriendRequestsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public MainFriendRequestsGuiItemProvider( final MainFriendRequestsGuiConfig config )
    {
        this.page = new MainFriendRequestsItemPage( config );
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
