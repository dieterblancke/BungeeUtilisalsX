package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class FriendActionsItemPage extends ItemPage
{

    public FriendActionsItemPage( final FriendActionsGuiConfig guiConfig, final FriendData friendData )
    {
        super( guiConfig.getRows() * 9 );
        final String currentServer = (( BungeeUtilisalsX ) BuX.getInstance()).getUserServerHelper().getCurrentServer(
                friendData.getFriend()
        ).orElse( null );

        final Object[] placeholders = new Object[]{
                "{friend-name}", friendData.getFriend(),
                "{last-online}", Utils.formatDate( friendData.getLastOnline() ),
                "{server}", currentServer == null ? "Unknown" : currentServer
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
                            currentServer,
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

    private GuiItem getFriendGuiItem( final FriendGuiConfigItem item, final FriendData friendData, final String currentServer, final Object... placeholders )
    {
        final String action = Utils.replacePlaceHolders( item.getAction().toLowerCase().trim(), placeholders );
        final String rightAction = Utils.replacePlaceHolders( item.getRightAction().toLowerCase().trim(), placeholders );
        final boolean online = currentServer != null;
        final ItemStack itemStack = online
                ? item.getOnlineItem().buildItem( placeholders )
                : item.getOfflineItem().buildItem( placeholders );

        if ( itemStack.getItemMeta() instanceof SkullMeta )
        {
            final SkullMeta itemMeta = ( (SkullMeta) itemStack.getItemMeta() );
            itemMeta.setOwner( friendData.getFriend() );
            itemStack.setItemMeta( itemMeta );
        }

        return this.getGuiItem( action, rightAction, itemStack );
    }
}
