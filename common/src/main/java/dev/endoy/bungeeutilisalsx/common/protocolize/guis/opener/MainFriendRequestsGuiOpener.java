package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.MainFriendRequestsGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.MainFriendRequestsGuiItemProvider;

public class MainFriendRequestsGuiOpener extends GuiOpener
{
    public MainFriendRequestsGuiOpener()
    {
        super( "friendrequests" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        final MainFriendRequestsGuiConfig config = DefaultGui.FRIENDREQUESTS.getConfig();
        final Gui gui = Gui.builder()
            .itemProvider( new MainFriendRequestsGuiItemProvider( user, config ) )
            .rows( config.getRows() )
            .title( config.getTitle() )
            .user( user )
            .build();

        gui.open();
    }
}
