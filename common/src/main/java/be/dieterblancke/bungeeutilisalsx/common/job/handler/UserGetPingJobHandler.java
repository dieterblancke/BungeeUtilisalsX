package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserGetPingJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;

public class UserGetPingJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeGetUserPingJob( final UserGetPingJob job )
    {
        job.getTargetUser().ifPresent( target -> BuX.getInstance().getJobManager().executeJob(
                new UserLanguageMessageJob(
                        job,
                        "general-commands.ping.other",
                        "{target}", target.getName(),
                        "{targetPing}", target.getPing()
                )
        ) );
    }
}
