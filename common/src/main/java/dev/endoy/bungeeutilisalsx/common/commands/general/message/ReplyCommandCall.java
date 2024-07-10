package dev.endoy.bungeeutilisalsx.common.commands.general.message;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCompleter;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PrivateMessageType;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserPrivateMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;

import java.util.List;

public class ReplyCommandCall implements CommandCall, TabCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user.isMsgToggled() )
        {
            user.sendLangMessage( "general-commands.msgtoggle.pm-cmd-disabled" );
            return;
        }
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "general-commands.reply.usage" );
            return;
        }
        if ( !user.getStorage().hasData( UserStorageKey.MSG_LAST_USER ) )
        {
            user.sendLangMessage( "general-commands.reply.no-target" );
            return;
        }

        final String name = user.getStorage().getData( UserStorageKey.MSG_LAST_USER );
        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final String message = String.join( " ", args );

            BuX.getInstance().getJobManager().executeJob( new UserPrivateMessageJob(
                    user.getUuid(),
                    user.getName(),
                    name,
                    message,
                    PrivateMessageType.REPLY
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
        return "Sends a reply to the user you lastly interacted privately with.";
    }

    @Override
    public String getUsage()
    {
        return "/reply (message)";
    }

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }
}
