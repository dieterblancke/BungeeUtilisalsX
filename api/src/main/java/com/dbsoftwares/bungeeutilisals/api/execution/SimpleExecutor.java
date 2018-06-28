package com.dbsoftwares.bungeeutilisals.api.execution;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleExecutor implements IExecutor {

    private static final ScheduledExecutorService executors = Executors.newSingleThreadScheduledExecutor();

    public void execute(SimpleJob executor) {
        executor.execute();
    }

    public void asyncExecute(SimpleJob executor) {
        executors.execute(executor::execute);
    }

    public void delayedExecute(int delay, SimpleJob executor) {
        executors.schedule(executor::execute, delay, TimeUnit.SECONDS);
    }
}