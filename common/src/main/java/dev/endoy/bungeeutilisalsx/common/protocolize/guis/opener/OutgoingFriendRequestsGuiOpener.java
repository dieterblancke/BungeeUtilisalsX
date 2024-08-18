package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequestType;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request.FriendRequestsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.request.FriendRequestsGuiItemProvider;

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
                .user( user )
                .build();

            gui.open();
        } );
    }
}
