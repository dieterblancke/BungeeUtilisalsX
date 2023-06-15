package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserAddFriendJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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
