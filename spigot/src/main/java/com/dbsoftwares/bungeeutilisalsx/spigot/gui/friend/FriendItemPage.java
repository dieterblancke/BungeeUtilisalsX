package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend;

import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.ItemPage;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;
import java.util.List;

public class FriendItemPage extends ItemPage
{

    public FriendItemPage( final int page, final int max, final FriendGuiConfig guiConfig, final List<FriendData> friendData )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( ( (FriendGuiConfigItem) item ).isFriendItem() )
            {
                continue;
            }
            if ( !this.shouldShow( page, max, item ) )
            {
                continue;
            }
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( item ) );
            }
        }

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( !( (FriendGuiConfigItem) item ).isFriendItem() )
            {
                continue;
            }

            final Iterator<FriendData> friendDataIterator = friendData.iterator();
            for ( int slot : item.getSlots() )
            {
                if ( !friendDataIterator.hasNext() )
                {
                    break;
                }
                final FriendData data = friendDataIterator.next();

                super.setItem( slot, this.getFriendGuiItem(
                        (FriendGuiConfigItem) item,
                        data,
                        "{friend-name}", data.getFriend(),
                        "{last-online}", Utils.formatDate( data.getLastOnline() ),
                        "{server}", "UNKNOWN" // TODO
                ) );
            }
        }
    }

    private GuiItem getFriendGuiItem( final FriendGuiConfigItem item, final FriendData friendData, final Object... placeholders )
    {
        final String action = item.getAction().toLowerCase().trim();
        final boolean online = false; // TODO: online check
        final ItemStack itemStack = online
                ? item.getOnlineItem().buildItem( placeholders )
                : item.getOfflineItem().buildItem( placeholders );

        if ( itemStack.getItemMeta() instanceof SkullMeta )
        {
            final SkullMeta itemMeta = ( (SkullMeta) itemStack.getItemMeta() );
            itemMeta.setOwner( friendData.getFriend() );
            itemStack.setItemMeta( itemMeta );
        }

        return this.getGuiItem( action, itemStack );
    }
}
