package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendactions;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfigItem;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;

import java.util.Optional;

public class FriendActionsItemPage extends ItemPage
{

    public FriendActionsItemPage( final User user, final FriendActionsGuiConfig guiConfig, final FriendData friendData )
    {
        super( guiConfig.getRows() * 9 );
        final String currentServer = Optional.ofNullable( BuX.getApi().getPlayerUtils().findPlayer( friendData.getFriend() ) )
                .map( IProxyServer::getName ).orElse( null );

        final MessagePlaceholders placeholders = MessagePlaceholders.create()
                .append( "friend-name", friendData.getFriend() )
                .append( "last-online", Utils.formatDate( friendData.getLastOnline() ) )
                .append( "server", currentServer == null ? "Unknown" : currentServer );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            for ( int slot : item.getSlots() )
            {
                if ( ( (FriendGuiConfigItem) item ).isFriendItem() )
                {
                    super.setItem( slot, this.getFriendGuiItem(
                            user,
                            (FriendGuiConfigItem) item,
                            friendData,
                            currentServer,
                            placeholders
                    ) );
                }
                else
                {
                    super.setItem( slot, this.getGuiItem(
                            user,
                            item,
                            placeholders
                    ) );
                }
            }
        }
    }

    private GuiItem getFriendGuiItem( final User user,
                                      final FriendGuiConfigItem item,
                                      final FriendData friendData,
                                      final String currentServer,
                                      final HasMessagePlaceholders placeholders )
    {
        final boolean online = currentServer != null;
        final ItemStack itemStack = online
                ? item.getOnlineItem().buildItem( user, placeholders )
                : item.getOfflineItem().buildItem( user, placeholders );

        if ( itemStack.itemType() == ItemType.PLAYER_HEAD )
        {
            itemStack.nbtData().putString( "SkullOwner", friendData.getFriend() );
        }

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }
}
