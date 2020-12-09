package com.dbsoftwares.bungeeutilisalsx.spigot.gui.opener;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.spigot.BungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.Gui;
import com.dbsoftwares.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiConfig;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.friendactions.FriendActionsGuiItemProvider;
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
                        .title( config.getTitle() )
                        .players( player )
                        .build();

                gui.open();
            }
        }
    }
}
