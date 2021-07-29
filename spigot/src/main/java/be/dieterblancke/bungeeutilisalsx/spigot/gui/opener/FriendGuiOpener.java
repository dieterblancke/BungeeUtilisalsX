package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiItemProvider;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.friend.FriendGuiUtils;
import org.bukkit.entity.Player;

import java.util.List;
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
        final List<FriendData> friends = FriendGuiUtils.getFriendData( player.getUniqueId() );

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            final FriendGuiConfig config = DefaultGui.FRIEND.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendGuiItemProvider( player, config, friends ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .players( player )
                    .build();

            gui.open();
        }
    }
}
