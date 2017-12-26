package com.dbsoftwares.bungeeutilisals.api.execution;

public interface IExecutor {

    /**
     * Executes a simple job. Can be anything.
     * @param executor The job you want to execute.
     */
    void execute(SimpleJob executor);

    /**
     * Executes a simple job async. Can be anything.
     * @param executor The job you want to execute async.
     */
    void asyncExecute(SimpleJob executor);

    /**
     * Executes a,n async delayed simple job. Can be anything.
     * @param executor The job you want to execute async delayed.
     */
    void delayedExecute(int delay, SimpleJob executor);

}