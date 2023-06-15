package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;

public class UserChatListener
{

    @Subscribe
    public void onChat( final PlayerChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );

        if ( optional.isEmpty() )
        {
            return;
        }
        final User user = optional.get();
        final UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
        BuX.getApi().getEventLoader().launchEvent( chatEvent );

        if ( chatEvent.getUser().allowsMessageModifications() )
        {
            if ( chatEvent.isCancelled() )
            {
                event.setResult( ChatResult.denied() );
                return;
            }

            if ( !event.getMessage().equals( chatEvent.getMessage() ) )
            {
                event.setResult( ChatResult.message( chatEvent.getMessage() ) );
            }
        }
    }

    @Subscribe
    public void onChat( final CommandExecuteEvent event )
    {
        if ( event.getCommandSource() instanceof Player player )
        {
            final Optional<User> optional = BuX.getApi().getUser( player.getUsername() );

            if ( optional.isEmpty() )
            {
                return;
            }
            final User user = optional.get();
            final UserCommandEvent commandEvent = new UserCommandEvent( user, event.getCommand() );
            BuX.getApi().getEventLoader().launchEvent( commandEvent );

            if ( commandEvent.getUser().allowsMessageModifications() )
            {
                if ( commandEvent.isCancelled() )
                {
                    event.setResult( CommandResult.denied() );
                    return;
                }

                if ( !event.getCommand().equals( commandEvent.getCommand() ) )
                {
                    event.setResult( CommandResult.command( commandEvent.getCommand() ) );
                }
            }
        }
    }
}