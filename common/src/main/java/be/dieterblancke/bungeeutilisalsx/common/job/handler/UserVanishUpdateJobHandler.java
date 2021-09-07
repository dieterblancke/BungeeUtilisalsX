package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserVanishUpdateJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;

public class UserVanishUpdateJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleUserVanishUpdate( final UserVanishUpdateJob job )
    {
        // update staff vanished value
        BuX.getApi().getStaffMembers()
                .stream()
                .filter( staffUser -> staffUser.getName().equalsIgnoreCase( job.getUserName() ) )
                .forEach( staffUser -> staffUser.setVanished( job.isVanished() ) );

        // update user vanished value
        job.getUser().ifPresent( user -> user.setVanished( job.isVanished() ) );
    }
}
