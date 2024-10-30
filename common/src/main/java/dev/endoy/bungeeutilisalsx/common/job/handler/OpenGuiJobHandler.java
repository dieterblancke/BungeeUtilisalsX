package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.OpenGuiJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;

public class OpenGuiJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeOpenGuiJob( final OpenGuiJob job )
    {
        if ( BuX.getInstance().isProtocolizeEnabled() )
        {
            job.getUserByName().ifPresent( user ->
                BuX.getInstance().getProtocolizeManager().getGuiManager().openGui( user, job.getGui(), job.getArgs() )
            );
        }
    }
}
