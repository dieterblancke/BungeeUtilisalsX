package com.dbsoftwares.bungeeutilisals.addon;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.Addon;
import com.dbsoftwares.bungeeutilisals.api.addon.IAddonTask;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Data
public class AddonTask implements Runnable, IAddonTask {

    private static AtomicInteger identifiers = new AtomicInteger(0);

    private final AddonScheduler scheduler;
    private final int id;
    private final Addon owner;
    private final Runnable task;
    private final long delay;
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AddonTask(AddonScheduler scheduler, Addon owner, Runnable task, long delay, long period, TimeUnit unit) {
        this.scheduler = scheduler;
        this.id = identifiers.incrementAndGet();
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis(delay);
        this.period = unit.toMillis(period);
    }

    @Override
    public void cancel() {
        boolean wasRunning = running.getAndSet(false);

        if (wasRunning) {
            scheduler.cancel(this);
        }
    }

    @Override
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        while (running.get()) {
            try {
                task.run();
            } catch (Throwable t) {
                BUCore.log(Level.SEVERE, String.format("Task %s encountered an exception", this), t);
            }

            if (period <= 0) {
                break;
            }

            try {
                Thread.sleep(period);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        cancel();
    }
}