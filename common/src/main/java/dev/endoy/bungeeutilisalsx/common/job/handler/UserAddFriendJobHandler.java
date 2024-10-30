package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserAddFriendJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

public class UserAddFriendJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleAddFriendJob( final UserAddFriendJob job )
    {
        job.getUser().ifPresent( user ->
        {
            user.sendLangMessage( "friends.accept.request-accepted", MessagePlaceholders.create().append( "user", job.getFriendName() ) );
            user.getFriends().add( job.getAsFriendData() );
        } );
    }
}
