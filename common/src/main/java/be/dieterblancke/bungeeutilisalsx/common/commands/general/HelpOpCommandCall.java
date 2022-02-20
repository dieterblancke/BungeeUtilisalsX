package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

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

        if ( !user.hasPermission( permission ) )
        {
            user.sendLangMessage(
                    "general-commands.helpop.broadcast",
                    "{message}", message,
                    "{user}", user.getName(),
                    "{user_server}", user.getServerName()
            );
        }

        BuX.getApi().langPermissionBroadcast(
                "general-commands.helpop.broadcast",
                permission,
                "{message}", message,
                "{user}", user.getName(),
                "{user_server}", user.getServerName()
        );
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
                "{user}", targetName,
                "{message}", message
        );

        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                targetName,
                "general-commands.helpop.reply-receive",
                "{user}", user.getName(),
                "{message}", message
        ) );
    }
}
