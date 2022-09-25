package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.MainFriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friendrequests.MainFriendRequestsGuiItemProvider;

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
