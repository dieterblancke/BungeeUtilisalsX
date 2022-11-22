package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserRemoveFriendJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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
