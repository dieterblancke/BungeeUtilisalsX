package com.dbsoftwares.bungeeutilisals.bungee.task;

/*
 * Created by DBSoftwares on 31 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.task.builder.ATask;
import com.dbsoftwares.bungeeutilisals.bungee.task.builder.TaskBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.md_5.bungee.api.plugin.Plugin;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskScheduler {

    private final Object obj = new Object();
    private AtomicInteger ids = new AtomicInteger(1);
    private final TIntObjectMap<Task> tasks = TCollections.synchronizedMap(new TIntObjectHashMap<Task>());
    private final Multimap<Plugin, Task> pluginTasks = Multimaps.synchronizedMultimap(HashMultimap.<Plugin, Task>create());
    private Executor executor = Executors.newCachedThreadPool();

    public Task start(TaskBuilder builder) {
        return start(builder.build());
    }

    public Task start(ATask task) {
        switch (task.taskType()) {
            default:
            case NOW: {
                return run(BungeeUtilisals.getInstance(), task, 0L, -1L);
            }
            case DELAY: {
                return run(BungeeUtilisals.getInstance(), task, task.delay(), -1L);
            }
            case REPEAT: {
                return run(BungeeUtilisals.getInstance(), task, task.delay(), task.repeat());
            }
        }
    }

    public void cancel(Task task) {
        task.cancel();
    }

    void cancel0(Task task) {
        synchronized (obj) {
            tasks.remove(task.getTaskId());
            pluginTasks.values().remove(task);
        }
    }

    public int cancel(Plugin plugin) {
        List<Task> toRemove = Lists.newArrayList(pluginTasks.get(plugin));

        for (Task task : toRemove) {
            cancel(task);
        }
        return toRemove.size();
    }

    public void cancel(int id) {
        tasks.get(id).cancel();
    }

    private Task run(Plugin plugin, ATask task, long delay, long period) {
        if (delay < 0L) {
            delay = 0;
        }
        if (period == 0L) {
            period = 1L;
        } else if (period < -1L) {
            period = -1L;
        }
        Task t = new Task(plugin, task.aSync(), ids.getAndIncrement(), task.task(), delay, period, task.time());
        if (task.aSync()) {
            executor.execute(t);
        } else {
            t.run();
        }
        return t;
    }
}