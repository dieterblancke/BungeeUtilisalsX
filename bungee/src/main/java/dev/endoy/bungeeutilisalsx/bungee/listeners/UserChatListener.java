package dev.endoy.bungeeutilisalsx.bungee.listeners;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class UserChatListener implements Listener
{

    @EventHandler
    public void onChat( ChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( ( (CommandSender) event.getSender() ).getName() );

        if ( optional.isEmpty() )
        {
            return;
        }
        final User user = optional.get();

        if ( event.isCommand() )
        {
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
        else
        {
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
    }
}