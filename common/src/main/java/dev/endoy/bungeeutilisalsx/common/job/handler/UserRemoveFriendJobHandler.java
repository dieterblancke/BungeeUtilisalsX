package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserRemoveFriendJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

public class UserRemoveFriendJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleRemoveFriendJob( final UserRemoveFriendJob job )
    {
        job.getUser().ifPresent( user ->
        {
            user.getFriends().removeIf( data -> data.getFriend().equalsIgnoreCase( job.getFriendName() ) );
            user.sendLangMessage( "friends.remove.friend-removed", MessagePlaceholders.create().append( "user", job.getFriendName() ) );
        } );
    }
}
