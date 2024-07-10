package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.BUEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ExecuteEventJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import lombok.SneakyThrows;

public class ExecuteEventJobHandler extends AbstractJobHandler
{

    @JobHandler
    @SneakyThrows
    void executeEventJobHandler( final ExecuteEventJob job )
    {
        final Class<?> clazz = Class.forName( job.getClassName() );
        final BUEvent event = (BUEvent) clazz.getConstructors()[0].newInstance( job.getParameters() );

        BuX.getApi().getEventLoader().launchEvent( event );
    }
}
