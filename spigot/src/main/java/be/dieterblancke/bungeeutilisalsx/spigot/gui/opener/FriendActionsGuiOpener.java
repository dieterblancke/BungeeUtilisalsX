package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiItemProvider;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.friend.FriendGuiUtils;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FriendActionsGuiOpener extends GuiOpener
{
    public FriendActionsGuiOpener()
    {
        super( "friendactions" );
    }

    @Override
    public void openGui( final Player player, final String[] args )
    {
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();
            final FriendData friendData = FriendGuiUtils.getFriendData( player.getUniqueId(), args[0] );

            if ( friendData != null )
            {
                final FriendActionsGuiConfig config = DefaultGui.FRIENDACTIONS.getConfig();
                final Gui gui = Gui.builder()
                        .itemProvider( new FriendActionsGuiItemProvider(
                                player,
                                config,
                                friendData
                        ) )
                        .rows( config.getRows() )
                        .title( config.getTitle().replace( "{friend-name}", friendData.getFriend() ) )
                        .players( player )
                        .build();

                gui.open();
            }
        }
    }
}
