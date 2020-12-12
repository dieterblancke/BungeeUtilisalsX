package com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions;

import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.ItemPage;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class FriendActionsItemPage extends ItemPage
{

    public FriendActionsItemPage( final FriendActionsGuiConfig guiConfig, final FriendData friendData )
    {
        super( guiConfig.getRows() * 9 );

        final Object[] placeholders = new Object[]{
                "{friend-name}", friendData.getFriend(),
                "{last-online}", Utils.formatDate( friendData.getLastOnline() ),
                "{server}", "UNKNOWN" // TODO
        };

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                if ( ( (FriendGuiConfigItem) item ).isFriendItem() )
                {
                    super.setItem( slot, this.getFriendGuiItem(
                            (FriendGuiConfigItem) item,
                            friendData,
                            placeholders
                    ) );
                }
                else
                {
                    super.setItem( slot, this.getGuiItem(
                            item,
                            placeholders
                    ) );
                }
            }
        }
    }

    private GuiItem getFriendGuiItem( final FriendGuiConfigItem item, final FriendData friendData, final Object... placeholders )
    {
        final String action = Utils.replacePlaceHolders( item.getAction().toLowerCase().trim(), placeholders );
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
