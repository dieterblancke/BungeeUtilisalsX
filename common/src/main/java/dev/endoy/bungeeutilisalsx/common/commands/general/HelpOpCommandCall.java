package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class HelpOpCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.helpop.usage" );
            return;
        }
        if ( args.get( 0 ).equalsIgnoreCase( "reply" ) && args.size() > 2 )
        {
            executeReplySubCommand( user, args );
            return;
        }
        final String message = String.join( " ", args );
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "helpop.receive-broadcast" );

        MessagePlaceholders broadcastPlaceholders = MessagePlaceholders.create()
                .append( "message", message )
                .append( "user", user.getName() )
                .append( "user_server", user.getServerName() );

        if ( !user.hasPermission( permission ) )
        {
            user.sendLangMessage( "general-commands.helpop.broadcast", broadcastPlaceholders );
        }

        BuX.getApi().langPermissionBroadcast( "general-commands.helpop.broadcast", permission, broadcastPlaceholders );
    }

    @Override
    public String getDescription()
    {
        return "Sends a helpop message to the online staff. Staff can reply using /helpop reply (user) (message).";
    }

    @Override
    public String getUsage()
    {
        return "/helpop [reply] (message)";
    }

    private void executeReplySubCommand( final User user, final List<String> args )
    {
        if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "helpop.reply-permission" ) ) )
        {
            user.sendLangMessage( "no-permission" );
            return;
        }

        final String targetName = args.get( 1 );
        final String message = String.join( " ", args.subList( 2, args.size() ) );

        if ( !BuX.getApi().getPlayerUtils().isOnline( targetName ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        user.sendLangMessage(
                "general-commands.helpop.reply-send",
                MessagePlaceholders.create()
                        .append( "user", targetName )
                        .append( "message", message )
        );

        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                targetName,
                "general-commands.helpop.reply-receive",
                MessagePlaceholders.create()
                        .append( "user", user.getName() )
                        .append( "message", message )
        ) );
    }
}
