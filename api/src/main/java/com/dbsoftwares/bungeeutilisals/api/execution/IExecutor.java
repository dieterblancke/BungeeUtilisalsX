package com.dbsoftwares.bungeeutilisals.api.execution;

import java.util.concurrent.ScheduledFuture;

public interface IExecutor {

    /**
     * Executes a simple job.
     * @param executor The job you want to execute.
     */
    void execute(SimpleJob executor);

    /**
     * Executes a simple job async.
     * @param executor The job you want to execute async.
     */
    void asyncExecute(SimpleJob executor);

    /**
     * Executes an async delayed simple job.
     * @param delay Time, in seconds, untill execution.
     * @param executor The job you want to execute async delayed.
     */
    void delayedExecute(int delay, SimpleJob executor);

    /**
     * Executes a,n async repeating simple job.
     *
     * @param delay    Time, in seconds, untill execution.
     * @param executor The job you want to execute async delayed.
     * @return an instance of ScheduledFuture
     */
    ScheduledFuture scheduledExecute(int delay, SimpleJob executor);
}