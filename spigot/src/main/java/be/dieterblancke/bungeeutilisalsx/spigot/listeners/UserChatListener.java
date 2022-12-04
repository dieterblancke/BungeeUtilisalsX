package be.dieterblancke.bungeeutilisalsx.spigot.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Optional;

public class UserChatListener implements Listener
{

    @EventHandler
    public void onChat( AsyncPlayerChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( optional.isEmpty() )
        {
            return;
        }
        final User user = optional.get();
        final UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
        BuX.getApi().getEventLoader().launchEvent( chatEvent );

        if ( chatEvent.isCancelled() )
        {
            event.setCancelled( true );
            return;
        }

        if ( !event.getMessage().equals( chatEvent.getMessage() ) )
        {
            event.setMessage( chatEvent.getMessage() );
        }
    }

    @EventHandler
    public void onCommand( PlayerCommandPreprocessEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( optional.isEmpty() )
        {
            return;
        }
        final User user = optional.get();
        final UserCommandEvent commandEvent = new UserCommandEvent( user, event.getMessage() );
        BuX.getApi().getEventLoader().launchEvent( commandEvent );

        if ( commandEvent.isCancelled() )
        {
            event.setCancelled( true );
            return;
        }
        if ( !event.getMessage().equals( commandEvent.getCommand() ) )
        {
            event.setMessage( commandEvent.getCommand() );
        }
    }
}