package com.dbsoftwares.bungeeutilisals.api.execution;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public class SimpleExecutor {

    private static final ExecutorService executors = Executors.newCachedThreadPool();

    public void execute(SimpleJob executor) {
        executor.execute();
    }

    public void asyncExecute(SimpleJob executor) {
        executors.execute(executor::execute);
    }

    public void delayedExecute(int delay, SimpleJob executor) {
        executors.execute(() -> {
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.execute();
        });
    }
}