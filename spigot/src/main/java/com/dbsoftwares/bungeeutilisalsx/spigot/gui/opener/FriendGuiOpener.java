package com.dbsoftwares.bungeeutilisalsx.spigot.gui.opener;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friend.FriendGuiItemProvider;
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
