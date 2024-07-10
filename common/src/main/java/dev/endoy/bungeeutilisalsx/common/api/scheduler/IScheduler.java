package dev.endoy.bungeeutilisalsx.common.api.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface IScheduler
{

    void runAsync( Runnable runnable );

    ScheduledFuture<?> runTaskDelayed( long delay, TimeUnit unit, Runnable runnable );

    ScheduledFuture<?> runTaskRepeating( long delay, long period, TimeUnit unit, Runnable runnable );

    default ScheduledFuture<?> runTaskDelayed( long delay,
                                               dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit unit,
                                               Runnable runnable )
    {
        return this.runTaskDelayed( delay, unit.toJavaTimeUnit(), runnable );
    }

    default ScheduledFuture<?> runTaskRepeating( long delay,
                                                 long period,
                                                 dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit unit,
                                                 Runnable runnable )
    {
        return this.runTaskRepeating( delay, period, unit.toJavaTimeUnit(), runnable );
    }

    Executor getExecutorService();
}
