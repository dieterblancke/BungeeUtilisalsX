package com.dbsoftwares.bungeeutilisals.bungee.task.builder;

/*
 * Created by DBSoftwares on 31 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.task.Task;
import com.dbsoftwares.bungeeutilisals.universal.enums.TimeUnit;

public abstract class ATask {

    public abstract BTask task();

    public abstract Boolean aSync();

    public abstract TaskType taskType();

    public abstract TimeUnit time();

    public abstract Integer delay();

    public abstract Integer repeat();

    public ATask start() {
        Task task = BungeeUtilisals.getInstance().getScheduler().start(this);

        task().setupId(task);
        return this;
    }
}