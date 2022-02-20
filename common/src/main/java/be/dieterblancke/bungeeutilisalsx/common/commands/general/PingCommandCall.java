package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserGetPingJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class PingCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.ping.message" );
        }
        else
        {
            final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.permission-other" );
            if ( permission != null
                    && !permission.isEmpty()
                    && !user.hasPermission( permission ) )
            {
                user.sendLangMessage( "no-permission", "%permission%", permission );
                return;
            }

            final String name = args.get( 0 );

            if ( BuX.getApi().getPlayerUtils().isOnline( name ) )
            {
                BuX.getInstance().getJobManager().executeJob(
                        new UserGetPingJob( user.getUuid(), user.getName(), name )
                );
            }
            else
            {
                user.sendLangMessage( "offline" );
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Shows your (or someone else's) current ping towards the current proxy.";
    }

    @Override
    public String getUsage()
    {
        return "/ping [user]";
    }
}
