package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.BUEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ExecuteNetworkStaffEventJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
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
