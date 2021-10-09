package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.CommandBlockerConfig.BlockedCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.CommandBlockerConfig.BlockedSubCommand;
import com.google.common.base.Strings;

public class UserCommandExecutor implements EventExecutor
{

    @Event
    public void onCommand( final UserCommandEvent event )
    {
        if ( !ConfigFiles.COMMAND_BLOCKER.isEnabled() )
        {
            return;
        }

        final String commmand = event.getActualCommand().replaceFirst( "/", "" );

        if ( isBlocked( event.getUser(), commmand, event.getArguments() ) )
        {
            event.setCancelled( true );
            event.getUser().sendLangMessage(
                    "command-disabled",
                    "{command}", commmand
            );
        }
    }

    @Event
    public void onListenerCommand( final UserCommandEvent event )
    {
        final String commmandName = event.getActualCommand().replaceFirst( "/", "" );

        BuX.getInstance().getCommandManager().findCommandByName( commmandName ).ifPresent( command ->
        {
            if ( command.isListenerBased() && !command.isDisabledInServer( event.getUser().getServerName() ) )
            {
                BuX.debug( "Executing listener command " + commmandName );
                event.setCancelled( true );
                command.execute( event.getUser(), event.getArguments() );
            }
        } );
    }

    private boolean isBlocked( final User user, final String command, final String[] args )
    {
        for ( BlockedCommand blockedCommand : ConfigFiles.COMMAND_BLOCKER.getBlockedCommands() )
        {
            if ( !Strings.isNullOrEmpty( user.getServerName() )
                    && !blockedCommand.getServers().isEmpty()
                    && blockedCommand.getServers().stream().noneMatch( s -> s.isInGroup( user.getServerName() ) ) )
            {
                continue;
            }
            if ( !Strings.isNullOrEmpty( blockedCommand.getBypassPermission() ) && user.hasPermission( blockedCommand.getBypassPermission() ) )
            {
                continue;
            }
            if ( !blockedCommand.getCommand().equalsIgnoreCase( command ) )
            {
                continue;
            }

            if ( blockedCommand.getSubCommands().isEmpty() )
            {
                return true;
            }
            else
            {
                for ( BlockedSubCommand blockedSubCommand : blockedCommand.getSubCommands() )
                {
                    if ( args.length < blockedSubCommand.getIndex() )
                    {
                        continue;
                    }
                    if ( blockedSubCommand.getCommand().equalsIgnoreCase( args[blockedSubCommand.getIndex() - 1] ) )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
