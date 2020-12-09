package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions;

import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.ItemPage;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.PageableItemProvider;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item.GuiItem;

import java.util.Optional;

public class FriendActionsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public FriendActionsGuiItemProvider( final FriendActionsGuiConfig config, final FriendData friendData )
    {
        this.page = new FriendActionsItemPage(
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
