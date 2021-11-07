package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.Optional;

public class FriendActionsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public FriendActionsGuiItemProvider( final User user, final FriendActionsGuiConfig config, final FriendData friendData )
    {
        this.page = new FriendActionsItemPage(
                user,
                config,
                friendData
        );
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
