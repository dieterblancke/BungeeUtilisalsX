package be.dieterblancke.bungeeutilisalsx.common.commands.general.message;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.MessageQueue;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OfflineMessageCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user.isMsgToggled() )
        {
            user.sendLangMessage( "general-commands.msgtoggle.pm-cmd-disabled" );
            return;
        }
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "general-commands.offlinemessage.usage" );
            return;
        }
        final String targetName = args.get( 0 );
        final String message = String.join( " ", args.subList( 1, args.size() ) );

        if ( BuX.getApi().getPlayerUtils().isOnline( targetName ) )
        {
            final Optional<Command> msgCommand = BuX.getInstance().getCommandManager().findCommandByName( "msg" );

            if ( ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean( "offlinemessage.execute-msg-if-online" ) && msgCommand.isPresent() )
            {
                msgCommand.get().execute( user, args, parameters );
            }
            else
            {
                user.sendLangMessage( "general-commands.offlinemessage.online" );
            }
            return;
        }

        final MessageQueue<QueuedMessage> messageQueue = BuX.getApi().getStorageManager().getDao().createMessageQueue();
        messageQueue.add( new QueuedMessage(
                -1,
                targetName,
                new QueuedMessage.Message(
                        "general-commands.offlinemessage.message",
                        "{user}", user.getName(),
                        "{time}", Utils.formatDate( new Date(), user.getLanguageConfig().getConfig() ),
                        "{message}", message
                ),
                "NAME"
        ) );
        user.sendLangMessage( "general-commands.offlinemessage.sent", "{user}", targetName );
    }
}
