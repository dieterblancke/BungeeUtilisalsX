package com.dbsoftwares.bungeeutilisals.bungee.task.builder;

/*
 * Created by DBSoftwares on 31 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.task.Task;

public abstract class BTask implements Runnable {

    private int taskId = -1;

    public synchronized void cancel() throws IllegalStateException {
        BungeeUtilisals.getInstance().getScheduler().cancel(getTaskId());
    }

    public synchronized int getTaskId() throws IllegalStateException {
        return taskId;
    }

    public Task setupId(Task task) {
        this.taskId = task.getTaskId();
        return task;
    }
}