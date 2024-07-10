package dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequest;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequestType;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfigItem;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;

import java.util.Iterator;
import java.util.List;

public class FriendRequestsItemPage extends ItemPage
{

    public FriendRequestsItemPage( final User user,
                                   final int page,
                                   final int max,
                                   final FriendRequestsGuiConfig guiConfig,
                                   final FriendRequestType type,
                                   final List<FriendRequest> friendRequests )
    {
        super( guiConfig.getRows() * 9 );

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( ( (FriendRequestGuiConfigItem) item ).isRequestItem() )
            {
                continue;
            }
            if ( !this.shouldShow( user, page, max, item ) )
            {
                continue;
            }
            for ( int slot : item.getSlots() )
            {
                super.setItem( slot, this.getGuiItem( user, item ) );
            }
        }

        for ( GuiConfigItem item : guiConfig.getItems() )
        {
            if ( !( (FriendRequestGuiConfigItem) item ).isRequestItem() )
            {
                continue;
            }

            final Iterator<FriendRequest> friendRequestIterator = friendRequests.iterator();
            for ( int slot : item.getSlots() )
            {
                if ( !friendRequestIterator.hasNext() )
                {
                    break;
                }
                final FriendRequest data = friendRequestIterator.next();

                super.setItem( slot, this.getFriendGuiItem(
                        user,
                        type,
                        (FriendRequestGuiConfigItem) item,
                        data,
                        MessagePlaceholders.create()
                                .append( "user-name", type == FriendRequestType.OUTGOING ? data.getFriendName() : data.getUserName() )
                                .append( "requested-at", Utils.formatDate( data.getRequestedAt() ) )
                ) );
            }
        }
    }

    private GuiItem getFriendGuiItem( final User user,
                                      final FriendRequestType type,
                                      final FriendRequestGuiConfigItem item,
                                      final FriendRequest requestData,
                                      final HasMessagePlaceholders placeholders )
    {
        final ItemStack itemStack = item.getItem().buildItem( user, placeholders );

        if ( itemStack.itemType() == ItemType.PLAYER_HEAD )
        {
            itemStack.nbtData().putString(
                    "SkullOwner",
                    type == FriendRequestType.OUTGOING ? requestData.getFriendName() : requestData.getUserName()
            );
        }

        return this.getGuiItem( item.getAction(), item.getRightAction(), itemStack, placeholders );
    }
}
