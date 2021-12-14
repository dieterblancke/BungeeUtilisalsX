package be.dieterblancke.bungeeutilisalsx.common.job;

import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobManager;

import java.util.concurrent.CompletableFuture;

public class SingleProxyJobManager extends JobManager
{

    @Override
    public CompletableFuture<Void> executeJob( final Job job )
    {
        this.handle( job );
        return CompletableFuture.completedFuture( null );
    }
}
