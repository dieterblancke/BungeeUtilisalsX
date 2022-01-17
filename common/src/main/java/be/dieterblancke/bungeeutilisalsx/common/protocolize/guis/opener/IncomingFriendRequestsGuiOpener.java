package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request.FriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request.FriendRequestsGuiItemProvider;

public class IncomingFriendRequestsGuiOpener extends GuiOpener
{

    public IncomingFriendRequestsGuiOpener()
    {
        super( "incomingfriendrequests" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() ).thenAccept( incomingRequests ->
        {
            final FriendRequestsGuiConfig config = DefaultGui.INCOMINGFRIENDREQUESTS.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendRequestsGuiItemProvider( user, FriendRequestType.INCOMING, config, incomingRequests ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .users( user )
                    .build();

            gui.open();
        } );
    }
}
