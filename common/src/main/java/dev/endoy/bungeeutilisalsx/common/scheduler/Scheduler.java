package dev.endoy.bungeeutilisalsx.common.scheduler;

import dev.endoy.bungeeutilisalsx.common.api.scheduler.IScheduler;

import java.util.concurrent.*;

public class Scheduler implements IScheduler
{

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    @Override
    public void runAsync( final Runnable runnable )
    {
        EXECUTOR_SERVICE.execute( runnable );
    }

    @Override
    public ScheduledFuture<?> runTaskDelayed( final long delay, final TimeUnit unit, final Runnable runnable )
    {
        return SCHEDULED_EXECUTOR_SERVICE.schedule( () -> EXECUTOR_SERVICE.execute( runnable ), delay, unit );
    }

    @Override
    public ScheduledFuture<?> runTaskRepeating( final long delay,
                                                final long period,
                                                final TimeUnit unit,
                                                final Runnable runnable )
    {
        return SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(
            () -> EXECUTOR_SERVICE.execute( runnable ), delay, period, unit
        );
    }

    @Override
    public Executor getExecutorService()
    {
        return EXECUTOR_SERVICE;
    }
}
