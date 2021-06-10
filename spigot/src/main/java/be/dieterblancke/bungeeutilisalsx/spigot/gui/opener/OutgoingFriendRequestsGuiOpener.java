package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request.FriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.request.FriendRequestsGuiItemProvider;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class OutgoingFriendRequestsGuiOpener extends GuiOpener
{
    public OutgoingFriendRequestsGuiOpener()
    {
        super( "outgoingfriendrequests" );
    }

    @Override
    public void openGui( final Player player, final String[] args )
    {
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();
            final List<FriendRequest> outgoingRequests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( user.getUuid() );
            final FriendRequestsGuiConfig config = DefaultGui.OUTGOINGFRIENDREQUESTS.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendRequestsGuiItemProvider( player, FriendRequestType.OUTGOING, config, outgoingRequests ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .players( player )
                    .build();

            gui.open();
        }
    }
}
