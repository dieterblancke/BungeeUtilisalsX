package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.ItemPage;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.PageableItemProvider;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.item.GuiItem;

import java.util.List;
import java.util.Optional;

public class FriendRequestsGuiItemProvider implements PageableItemProvider
{

    private final ItemPage[] pages;

    public FriendRequestsGuiItemProvider( final User user,
                                          final FriendRequestType type,
                                          final FriendRequestsGuiConfig config,
                                          final List<FriendRequest> friendRequests )
    {
        final int itemsPerPage = config.getItems().stream()
                .filter( item -> ( (FriendRequestGuiConfigItem) item ).isRequestItem() )
                .mapToInt( item -> item.getSlots().size() )
                .sum();
        int pages = (int) Math.ceil( (double) friendRequests.size() / (double) itemsPerPage );
        if ( pages == 0 )
        {
            pages = 1;
        }
        this.pages = new ItemPage[pages];

        for ( int i = 0; i < pages; i++ )
        {
            final int max = ( i + 1 ) * itemsPerPage;

            this.pages[i] = new FriendRequestsItemPage(
                    user,
                    i,
                    pages,
                    config,
                    type,
                    friendRequests.isEmpty()
                            ? friendRequests
                            : friendRequests.size() <= max ? friendRequests : friendRequests.subList( i * itemsPerPage, max )
            );
        }
    }

    @Override
    public Optional<GuiItem> getItemAtSlot( final int page, final int rawSlot )
    {
        return this.getItemContents( page ).getItem( rawSlot );
    }

    @Override
    public ItemPage getItemContents( int page )
    {
        if ( page == 0 )
        {
            page = 1;
        }
        if ( page > pages.length )
        {
            page = pages.length;
        }
        return pages[page - 1];
    }

    @Override
    public int getPageAmount()
    {
        return pages.length;
    }
}
