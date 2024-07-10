package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendactions;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

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
