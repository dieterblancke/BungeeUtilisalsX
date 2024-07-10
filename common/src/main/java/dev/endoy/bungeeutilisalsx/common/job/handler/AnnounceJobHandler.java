package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.api.job.jobs.AnnounceJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.commands.general.AnnounceCommandCall;

public class AnnounceJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeAnnounceJob( final AnnounceJob job )
    {
        AnnounceCommandCall.sendAnnounce( job.getTypes(), job.getMessage() );
    }
}
