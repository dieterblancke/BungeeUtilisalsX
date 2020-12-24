package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.PageableItemProvider;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FriendActionsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage page;

    public FriendActionsGuiItemProvider( final Player player, final FriendActionsGuiConfig config, final FriendData friendData )
    {
        this.page = new FriendActionsItemPage(
                player,
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
