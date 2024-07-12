package dev.endoy.bungeeutilisalsx.common.commands.friends.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendSetting;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.FriendBroadcastJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;
import java.util.stream.Collectors;

public class FriendBroadcastSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.broadcast.usage" );
            return;
        }
        if ( !user.getFriendSettings().getSetting( FriendSetting.FRIEND_BROADCAST ) )
        {
            user.sendLangMessage( "friends.broadcast.disabled" );
            return;
        }

        final String message = String.join( " ", args );
        BuX.getInstance().getJobManager().executeJob( new FriendBroadcastJob(
                user.getUuid(),
                user.getName(),
                message,
                user.getFriends()
                        .stream()
                        .map( FriendData::getFriend )
                        .collect( Collectors.toList() )
        ) );
    }

    @Override
    public String getDescription()
    {
        return "Broadcasts a message to all your online friends.";
    }

    @Override
    public String getUsage()
    {
        return "/friend broadcast (message)";
    }
}
