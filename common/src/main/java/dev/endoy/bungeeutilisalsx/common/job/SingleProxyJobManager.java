package dev.endoy.bungeeutilisalsx.common.job;

import dev.endoy.bungeeutilisalsx.common.api.job.Job;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobManager;

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
