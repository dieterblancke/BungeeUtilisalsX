package dev.endoy.bungeeutilisalsx.common.commands.general.message;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PrivateMessageType;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserPrivateMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;

import java.util.List;

public class MsgCommandCall implements CommandCall
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
            user.sendLangMessage( "general-commands.msg.usage" );
            return;
        }
        final String name = args.get( 0 );

        if ( user.getName().equalsIgnoreCase( name ) )
        {
            user.sendLangMessage( "general-commands.msg.self-msg" );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final String message = String.join( " ", args.subList( 1, args.size() ) );

            user.getStorage().setData( UserStorageKey.MSG_LAST_USER, name );

            BuX.getInstance().getJobManager().executeJob( new UserPrivateMessageJob(
                user.getUuid(),
                user.getName(),
                name,
                message,
                PrivateMessageType.MSG
            ) );
        }
        else
        {
            user.sendLangMessage( "offline" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Sends a private message to a user.";
    }

    @Override
    public String getUsage()
    {
        return "/msg (user) (message)";
    }
}
