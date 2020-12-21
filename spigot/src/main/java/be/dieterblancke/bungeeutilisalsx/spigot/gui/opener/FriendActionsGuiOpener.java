package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiItemProvider;
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
            final FriendData friendData = user.getFriends().stream()
                    .filter( d -> d.getFriend().equalsIgnoreCase( args[0] ) )
                    .findFirst()
                    .orElse( user.getFriends().stream().findFirst().orElse( null ) );

            if ( friendData != null )
            {
                final FriendActionsGuiConfig config = ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().getFriendActionsGuiConfig();
                final Gui gui = Gui.builder()
                        .itemProvider( new FriendActionsGuiItemProvider(
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
