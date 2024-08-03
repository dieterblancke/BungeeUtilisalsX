package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserSwitchServerJob;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class ServerCommandCall implements CommandCall
{

    public static void sendToServer( final User user, final IProxyServer server )
    {
        MessagePlaceholders placeholders = MessagePlaceholders.create()
            .append( "server", server.getName() );

        if ( user.getServerName().equalsIgnoreCase( server.getName() ) )
        {
            user.sendLangMessage( "general-commands.server.alreadyconnected", placeholders );
            return;
        }

        user.sendToServer( server );
        user.sendLangMessage( "general-commands.server.connecting", placeholders );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.server.usage", MessagePlaceholders.create().append( "server", user.getServerName() ) );
            return;
        }

        final int serverArgIdx = args.size() == 2 ? 1 : 0;
        final IProxyServer server = BuX.getInstance().serverOperations().getServerInfo( args.get( serverArgIdx ) );

        if ( server == null )
        {
            user.sendLangMessage( "general-commands.server.notfound", MessagePlaceholders.create().append( "server", args.get( serverArgIdx ) ) );
            return;
        }

        if ( args.size() == 2 )
        {
            if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.permission-other" ) ) )
            {
                user.sendLangMessage( "no-permission" );
                return;
            }

            final String name = args.get( 0 );

            if ( BuX.getApi().getPlayerUtils().isOnline( name ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserSwitchServerJob( name, server.getName() ) );

                user.sendLangMessage(
                    "general-commands.server.sent-other",
                    MessagePlaceholders.create()
                        .append( "user", name )
                        .append( "server", server.getName() )
                );
            }
            else
            {
                user.sendLangMessage( "offline" );
            }
        }
        else
        {
            sendToServer( user, server );
        }
    }

    @Override
    public String getDescription()
    {
        return "Allows you to switch yourself, or someone else, to another server.";
    }

    @Override
    public String getUsage()
    {
        return "/server [user] (serverName)";
    }
}
