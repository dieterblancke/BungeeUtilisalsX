package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.BUEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ExecuteEventJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
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
