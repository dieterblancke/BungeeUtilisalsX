package be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfigItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class FriendActionsItemPage extends ItemPage
{

    public FriendActionsItemPage( final Player player, final FriendActionsGuiConfig guiConfig, final FriendData friendData )
    {
        super( guiConfig.getRows() * 9 );
        final String currentServer = ( (BungeeUtilisalsX) BuX.getInstance() ).getUserServerHelper().getCurrentServer(
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
                            player,
                            (FriendGuiConfigItem) item,
                            friendData,
                            currentServer,
                            placeholders
                    ) );
                }
                else
                {
                    super.setItem( slot, this.getGuiItem(
                            player,
                            item,
                            placeholders
                    ) );
                }
            }
        }
    }

    private GuiItem getFriendGuiItem( final Player player,
                                      final FriendGuiConfigItem item,
                                      final FriendData friendData,
                                      final String currentServer,
                                      final Object... placeholders )
    {
        final boolean online = currentServer != null;
        final ItemStack itemStack = online
                ? item.getOnlineItem().buildItem( player, placeholders )
                : item.getOfflineItem().buildItem( player, placeholders );

        if ( itemStack.getItemMeta() instanceof SkullMeta )
        {
            final SkullMeta itemMeta = ( (SkullMeta) itemStack.getItemMeta() );
            itemMeta.setOwner( friendData.getFriend() );
            itemStack.setItemMeta( itemMeta );
        }

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }
}
