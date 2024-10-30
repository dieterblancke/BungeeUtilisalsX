package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendactions.FriendActionsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendactions.FriendActionsGuiItemProvider;

public class FriendActionsGuiOpener extends GuiOpener
{
    public FriendActionsGuiOpener()
    {
        super( "friendactions" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        final FriendData friendData = user.getFriends()
            .stream()
            .filter( d -> d.getFriend().equalsIgnoreCase( args[0] ) )
            .findFirst()
            .orElse( null );

        if ( friendData != null )
        {
            final FriendActionsGuiConfig config = DefaultGui.FRIENDACTIONS.getConfig();
            final Gui gui = Gui.builder()
                .itemProvider( new FriendActionsGuiItemProvider(
                    user,
                    config,
                    friendData
                ) )
                .rows( config.getRows() )
                .title( config.getTitle().replace( "{friend-name}", friendData.getFriend() ) )
                .user( user )
                .build();

            gui.open();
        }
    }
}
