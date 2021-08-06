package be.dieterblancke.bungeeutilisalsx.common.job;

import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobManager;

public class SingleProxyJobManager extends JobManager
{

    @Override
    public void executeJob( final Job job )
    {
        this.handle( job );
    }
}
