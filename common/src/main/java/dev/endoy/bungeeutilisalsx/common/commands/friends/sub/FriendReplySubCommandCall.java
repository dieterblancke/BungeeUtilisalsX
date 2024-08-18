package dev.endoy.bungeeutilisalsx.common.commands.friends.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.PrivateMessageType;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserFriendPrivateMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;

public class FriendReplySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.reply.usage" );
            return;
        }
        if ( !user.getStorage().hasData( UserStorageKey.FRIEND_MSG_LAST_USER ) )
        {
            user.sendLangMessage( "friends.reply.no-target" );
            return;
        }

        final String name = user.getStorage().getData( UserStorageKey.FRIEND_MSG_LAST_USER );
        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage(
                "friends.reply.not-friend",
                MessagePlaceholders.create().append( "user", name )
            );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final String message = String.join( " ", args );

            BuX.getInstance().getJobManager().executeJob( new UserFriendPrivateMessageJob(
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
        return "Allows you to reply to a friend that messaged you earlier.";
    }

    @Override
    public String getUsage()
    {
        return "/friend reply (message)";
    }
}
