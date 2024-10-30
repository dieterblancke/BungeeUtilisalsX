package dev.endoy.bungeeutilisalsx.common.api.job.management;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.Job;

public class AbstractJobHandler
{

    protected void executeJob( final Job job )
    {
        BuX.getInstance().getJobManager().executeJob( job );
    }
}
