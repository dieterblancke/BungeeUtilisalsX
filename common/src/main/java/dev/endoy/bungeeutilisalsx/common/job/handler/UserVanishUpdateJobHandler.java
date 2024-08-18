package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserVanishUpdateJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;

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
