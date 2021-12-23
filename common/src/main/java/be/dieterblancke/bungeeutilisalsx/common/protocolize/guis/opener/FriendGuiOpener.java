package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiItemProvider;

import java.util.List;

public class FriendGuiOpener extends GuiOpener
{

    public FriendGuiOpener()
    {
        super( "friend" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        final List<FriendData> friends = user.getFriends();
        final FriendGuiConfig config = DefaultGui.FRIEND.getConfig();
        final Gui gui = Gui.builder()
                .itemProvider( new FriendGuiItemProvider( user, config, friends ) )
                .rows( config.getRows() )
                .title( config.getTitle() )
                .users( user )
                .build();

        gui.open();
    }
}
