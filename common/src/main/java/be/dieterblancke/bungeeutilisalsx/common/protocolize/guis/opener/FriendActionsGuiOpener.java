package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.opener;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friend.FriendGuiUtils;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendactions.FriendActionsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.friendactions.FriendActionsGuiItemProvider;

public class FriendActionsGuiOpener extends GuiOpener
{
    public FriendActionsGuiOpener()
    {
        super( "friendactions" );
    }

    @Override
    public void openGui( final User user, final String[] args )
    {
        final FriendData friendData = FriendGuiUtils.getFriendData( user.getUuid(), args[0] );

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
                    .users( user )
                    .build();

            gui.open();
        }
    }
}
