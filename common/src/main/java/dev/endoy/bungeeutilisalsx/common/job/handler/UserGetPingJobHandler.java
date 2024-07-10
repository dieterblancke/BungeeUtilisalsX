package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserGetPingJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

public class UserGetPingJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeGetUserPingJob( final UserGetPingJob job )
    {
        job.getTargetUser().ifPresent( target -> BuX.getInstance().getJobManager().executeJob(
                new UserLanguageMessageJob(
                        job,
                        "general-commands.ping.other",
                        MessagePlaceholders.create()
                                .append( "target", target.getName() )
                                .append( "targetPing", target.getPing() )
                )
        ) );
    }
}
