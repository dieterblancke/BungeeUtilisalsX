package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserPrivateMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.CommandSpyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpyEventExecutor implements EventExecutor
{

    @Event
    public void onPrivateMessage( final UserPrivateMessageEvent event )
    {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "socialspy.permission" );
        final List<User> users = Stream.concat( BuX.getApi().getUsers().stream(), Stream.of( BuX.getApi().getConsoleUser() ) )
                .filter( user -> user.isSocialSpy() && user.hasPermission( permission ) )
                .filter( user -> !user.getName().equals( event.getSender() )
                        && !user.getName().equals( event.getReceiver() ) )
                .collect( Collectors.toList() );

        if ( users.isEmpty() )
        {
            return;
        }

        for ( User user : users )
        {
            user.sendLangMessage(
                    "general-commands.socialspy.message",
                    MessagePlaceholders.create()
                            .append( "sender}", event.getSender() )
                            .append( "receiver", event.getReceiver() )
                            .append( "message", event.getMessage() )
            );
        }
    }

    @Event
    public void onCommand( final UserCommandEvent event )
    {
        final String commandName = event.getActualCommand().replaceFirst( "/", "" );

        for ( String command : ConfigFiles.GENERALCOMMANDS.getConfig().getStringList( "commandspy.ignored-commands" ) )
        {
            if ( command.trim().equalsIgnoreCase( commandName.trim() ) )
            {
                return;
            }
        }

        BuX.getInstance().getJobManager().executeJob( new CommandSpyJob(
                event.getUser().getUuid(),
                event.getUser().getName(),
                event.getUser().getServerName(),
                event.getCommand()
        ) );
    }
}
