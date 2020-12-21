package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiItemProvider;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FriendGuiOpener extends GuiOpener
{
    public FriendGuiOpener()
    {
        super( "friend" );
    }

    @Override
    public void openGui( final Player player, final String[] args )
    {
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            final FriendGuiConfig config = ( (BungeeUtilisalsX) BuX.getInstance() ).getGuiManager().getFriendGuiConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendGuiItemProvider( config, user.getFriends() ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .players( player )
                    .build();

            gui.open();
        }
    }
}
