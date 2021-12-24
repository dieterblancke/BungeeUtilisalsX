package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.OpenGuiJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;

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
