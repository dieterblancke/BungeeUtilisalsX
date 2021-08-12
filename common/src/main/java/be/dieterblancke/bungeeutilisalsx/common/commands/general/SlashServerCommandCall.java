package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserSwitchServerJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class SlashServerCommandCall implements CommandCall
{

    private final String serverName;

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( serverName );

        if ( server == null )
        {
            user.sendLangMessage( "general-commands.server.notfound", "{server}", serverName );
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
                        "{user}", name,
                        "{server}", server.getName()
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

    private void sendToServer( final User user, final IProxyServer server )
    {
        if ( user.getServerName().equalsIgnoreCase( server.getName() ) )
        {
            user.sendLangMessage( "general-commands.server.alreadyconnected", "{server}", server.getName() );
            return;
        }

        user.sendToServer( server );
        user.sendLangMessage( "general-commands.server.connecting", "{server}", server.getName() );
    }
}