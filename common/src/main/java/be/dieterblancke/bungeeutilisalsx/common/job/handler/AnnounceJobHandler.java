package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.AnnounceJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.AnnounceCommandCall;

public class AnnounceJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeAnnounceJob( final AnnounceJob job )
    {
        AnnounceCommandCall.sendAnnounce( job.getTypes(), job.getMessage() );
    }
}
