package dev.endoy.bungeeutilisalsx.common.protocolize.guis.opener;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.Gui;
import dev.endoy.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiConfig;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.friends.friend.FriendGuiItemProvider;

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
            .user( user )
            .build();

        gui.open();
    }
}
