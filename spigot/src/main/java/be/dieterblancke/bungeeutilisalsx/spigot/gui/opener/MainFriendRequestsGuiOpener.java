package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.MainFriendRequestsGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friendrequests.MainFriendRequestsGuiItemProvider;
import org.bukkit.entity.Player;

import java.util.Optional;

public class MainFriendRequestsGuiOpener extends GuiOpener
{
    public MainFriendRequestsGuiOpener()
    {
        super( "friendrequests" );
    }

    @Override
    public void openGui( final Player player, final String[] args )
    {
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();
            final MainFriendRequestsGuiConfig config = DefaultGui.FRIENDREQUESTS.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new MainFriendRequestsGuiItemProvider( config ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .players( player )
                    .build();

            gui.open();
        }
    }
}
