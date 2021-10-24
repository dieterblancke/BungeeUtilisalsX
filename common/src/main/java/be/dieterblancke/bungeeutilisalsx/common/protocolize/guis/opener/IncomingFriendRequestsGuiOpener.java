package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request.FriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request.FriendRequestsGuiItemProvider;

import java.util.List;

public class IncomingFriendRequestsGuiOpener extends GuiOpener
{

    public IncomingFriendRequestsGuiOpener()
    {
        super( "incomingfriendrequests" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        final List<FriendRequest> incomingRequests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() );
        final FriendRequestsGuiConfig config = DefaultGui.INCOMINGFRIENDREQUESTS.getConfig();
        final Gui gui = Gui.builder()
                .itemProvider( new FriendRequestsGuiItemProvider( user, FriendRequestType.INCOMING, config, incomingRequests ) )
                .rows( config.getRows() )
                .title( config.getTitle() )
                .users( user )
                .build();

        gui.open();
    }
}
