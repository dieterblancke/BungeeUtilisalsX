package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.FriendBroadcastJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;

public class FriendBroadcastJobHandler
{

    @JobHandler
    void handleFriendBroadcastJob( final FriendBroadcastJob job )
    {
        job.getReceivers().forEach( user -> user.sendLangMessage(
                "friends.broadcast.message",
                "{user}", job.getSenderName(),
                "{message}", job.getMessage()
        ) );
    }
}
