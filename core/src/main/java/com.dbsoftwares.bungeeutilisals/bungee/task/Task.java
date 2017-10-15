package com.dbsoftwares.bungeeutilisals.bungee.task;

/*
 * Created by DBSoftwares on 31 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.task.builder.BTask;
import com.dbsoftwares.bungeeutilisals.universal.enums.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Runnable {

    @Getter @Setter private volatile long period;
    @Getter @Setter private volatile long delay;
    @Getter private BTask task;
    private Boolean async;
    private Plugin plugin;
    private int id;
    private final AtomicBoolean running = new AtomicBoolean(true);

    Task(Plugin plugin, Boolean async, int id, BTask task, long delay, long period, TimeUnit unit) {
        this.plugin = plugin;
        this.async = async;
        this.task = task;
        this.id = id;
        this.delay = unit.toMillis( delay );
        this.period = unit.toMillis( period );
    }

    public Boolean isSync() {
        return !async;
    }

    public Boolean isASync() {
        return async;
    }

    public int getTaskId() {
        return id;
    }

    public Plugin getOwner() {
        return plugin;
    }

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
                System.out.println("[BungeeUtilisals] An error occured in task #" + id + "!");
                t.printStackTrace();
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

    public void cancel() {
        Boolean running = this.running.getAndSet( false );

        if (running) {
            BungeeUtilisals.getInstance().getScheduler().cancel0(this);
        }
    }
}