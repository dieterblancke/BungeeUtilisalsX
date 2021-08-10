package be.dieterblancke.bungeeutilisalsx.common.api.job.management;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;

public class AbstractJobHandler
{

    protected void executeJob( final Job job )
    {
        BuX.getInstance().getJobManager().executeJob( job );
    }
}
