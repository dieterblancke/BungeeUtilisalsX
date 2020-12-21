package be.dieterblancke.bungeeutilisalsx.spigot.gui.friend;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.item.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

        final Map<String, Optional<String>> userServers = ( (BungeeUtilisalsX) BuX.getInstance() ).getUserServerHelper().getCurrentServer(
                friendData.stream().map( FriendData::getFriend ).collect( Collectors.toList() )
        );

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
                final String currentServer = userServers.get( data.getFriend() ).orElse( null );

                super.setItem( slot, this.getFriendGuiItem(
                        (FriendGuiConfigItem) item,
                        data,
                        currentServer,
                        "{friend-name}", data.getFriend(),
                        "{last-online}", Utils.formatDate( data.getLastOnline() ),
                        "{server}", currentServer == null ? "Unknown" : currentServer
                ) );
            }
        }
    }

    private GuiItem getFriendGuiItem( final FriendGuiConfigItem item, final FriendData friendData, final String currentServer, final Object... placeholders )
    {
        final String action = Utils.replacePlaceHolders( item.getAction().toLowerCase().trim(), placeholders );
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

        return this.getGuiItem( action, itemStack );
    }
}
