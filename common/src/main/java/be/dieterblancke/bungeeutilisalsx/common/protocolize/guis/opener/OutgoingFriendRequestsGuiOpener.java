package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request.FriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendrequests.request.FriendRequestsGuiItemProvider;

public class OutgoingFriendRequestsGuiOpener extends GuiOpener
{

    public OutgoingFriendRequestsGuiOpener()
    {
        super( "outgoingfriendrequests" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( user.getUuid() ).thenAccept( outgoingRequests ->
        {
            final FriendRequestsGuiConfig config = DefaultGui.OUTGOINGFRIENDREQUESTS.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendRequestsGuiItemProvider( user, FriendRequestType.OUTGOING, config, outgoingRequests ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .users( user )
                    .build();

            gui.open();
        } );
    }
}
