package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.AddFriendJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;

public class AddFriendJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleAddFriendJob( final AddFriendJob job )
    {
        job.getUser().ifPresent( user ->
        {
            user.sendLangMessage( "friends.accept.request-accepted", "{user}", job.getFriendName() );
            user.getFriends().add( job.getAsFriendData() );
        } );
    }
}
