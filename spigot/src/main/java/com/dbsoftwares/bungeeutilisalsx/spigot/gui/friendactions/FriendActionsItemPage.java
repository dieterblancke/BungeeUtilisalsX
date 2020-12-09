package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions;

import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.ItemPage;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;

public class FriendActionsItemPage extends ItemPage
{

    public FriendActionsItemPage( final FriendActionsGuiConfig guiConfig, final FriendData friendData )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                if ( ( (FriendGuiConfigItem) item ).isFriendItem() )
                {
                    super.setItem( slot, this.getFriendGuiItem(
                            (FriendGuiConfigItem) item,
                            friendData,
                            "{friend-name}", friendData.getFriend(),
                            "{last-online}", Utils.formatDate( friendData.getLastOnline() ),
                            "{server}", "UNKNOWN" // TODO
                    ) );
                }
                else
                {
                    super.setItem( slot, this.getGuiItem( item ) );
                }
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
