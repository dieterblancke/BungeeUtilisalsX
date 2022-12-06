package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserSwitchServerJob;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SlashServerCommandCall implements CommandCall
{

    private final String serverName;

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final IProxyServer server = BuX.getInstance().serverOperations().getServerInfo( serverName );

        if ( server == null )
        {
            user.sendLangMessage(
                    "general-commands.server.notfound",
                    MessagePlaceholders.create().append( "server", serverName )
            );
            return;
        }

        if ( args.size() == 1 )
        {
            if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.permission-other" ) ) )
            {
                user.sendLangMessage( "no-permission" );
                return;
            }

            final String name = args.get( 0 );

            if ( BuX.getApi().getPlayerUtils().isOnline( name ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserSwitchServerJob(
                        name,
                        server.getName()
                ) );

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
            return;
        }

        sendToServer( user, server );
    }

    @Override
    public String getDescription()
    {
        return "A shorthand notation for /server, if enabled.";
    }

    @Override
    public String getUsage()
    {
        return "/(serverName) [user]";
    }

    private void sendToServer( final User user, final IProxyServer server )
    {
        if ( user.getServerName().equalsIgnoreCase( server.getName() ) )
        {
            user.sendLangMessage(
                    "general-commands.server.alreadyconnected",
                    MessagePlaceholders.create()
                            .append( "server", server.getName() )
            );
            return;
        }

        user.sendToServer( server );
        user.sendLangMessage(
                "general-commands.server.connecting",
                MessagePlaceholders.create()
                        .append( "server", server.getName() )
        );
    }
}