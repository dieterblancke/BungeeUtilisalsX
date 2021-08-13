package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
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

        if ( isBlocked( event.getUser().getServerName(), commmand, event.getArguments() ) )
        {
            event.setCancelled( true );
            event.getUser().sendLangMessage(
                    "command-disabled",
                    "{command}", commmand
            );
        }
    }

    private boolean isBlocked( final String server, final String command, final String[] args )
    {
        for ( BlockedCommand blockedCommand : ConfigFiles.COMMAND_BLOCKER.getBlockedCommands() )
        {
            if ( !Strings.isNullOrEmpty( server )
                    && !blockedCommand.getServers().isEmpty()
                    && blockedCommand.getServers().stream().noneMatch( s -> s.isInGroup( server ) ) )
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
