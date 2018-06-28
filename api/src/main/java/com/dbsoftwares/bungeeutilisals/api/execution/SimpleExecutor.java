package com.dbsoftwares.bungeeutilisals.api.execution;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimpleExecutor implements IExecutor {

    private static final ScheduledExecutorService executors = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void execute(SimpleJob executor) {
        executor.execute();
    }

    @Override
    public void asyncExecute(SimpleJob executor) {
        executors.execute(executor::execute);
    }

    @Override
    public void delayedExecute(int delay, SimpleJob executor) {
        executors.schedule(executor::execute, delay, TimeUnit.SECONDS);
    }

    @Override
    public ScheduledFuture scheduledExecute(int delay, SimpleJob executor) {
        return executors.scheduleAtFixedRate(executor::execute, delay, delay, TimeUnit.SECONDS);
    }
}