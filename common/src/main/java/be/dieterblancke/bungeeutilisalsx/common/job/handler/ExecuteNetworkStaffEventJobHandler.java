package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.BUEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ExecuteNetworkStaffEventJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class ExecuteNetworkStaffEventJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void executeNetworkStaffEventJobHandler( final ExecuteNetworkStaffEventJob job )
    {
        final Class<?> clazz = Class.forName( job.getClassName() );
        final BUEvent event = (BUEvent) clazz.getConstructors()[0].newInstance( job.getUserName(), job.getUuid(), job.getStaffRank() );

        BuX.getApi().getEventLoader().launchEvent( event );
    }
}
