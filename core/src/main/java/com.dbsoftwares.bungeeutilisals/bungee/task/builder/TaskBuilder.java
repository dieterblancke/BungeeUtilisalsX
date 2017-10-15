package com.dbsoftwares.bungeeutilisals.bungee.task.builder;

/*
 * Created by DBSoftwares on 31 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.universal.utilities.MathUtilities;
import com.dbsoftwares.bungeeutilisals.universal.enums.TimeUnit;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaskBuilder {

    BTask task;
    Boolean aSync;
    TaskType taskType;
    TimeUnit unit;
    Integer delay = -1;
    Integer repeat = -1;

    public TaskBuilder() {}

    public TaskBuilder(String settings) {
        settings = settings.toLowerCase();

        LinkedHashMap<Integer, String> args = Maps.newLinkedHashMap();
        Integer i = 0;
        for (String arg : settings.split(" ")) {
            args.put(i, arg);
            i++;
        }

        for (Map.Entry<Integer, String> entry : args.entrySet()) {
            Integer key = entry.getKey();
            String arg = entry.getValue();

            if(arg.equalsIgnoreCase("SYNC")) {
                aSync = false;
            } else if(arg.equalsIgnoreCase("ASYNC")) {
                aSync = true;
            } else if(arg.equalsIgnoreCase("NOW") || arg.equalsIgnoreCase("INSTANT")) {
                taskType = TaskType.NOW;
            } else if(arg.equalsIgnoreCase("DELAY") || arg.equalsIgnoreCase("DELAYED")) {
                taskType = TaskType.DELAY;
            } else if(arg.equalsIgnoreCase("REPEAT") || arg.equalsIgnoreCase("REPEATING")) {
                taskType = TaskType.REPEAT;
            } else if(TimeUnit.isUnit(arg)) {
                unit = TimeUnit.valueOf(arg.toUpperCase());
            } else if(MathUtilities.isInteger(arg)) {
                Integer integer = Integer.parseInt(arg);

                if (args.containsKey(key - 1) && MathUtilities.isInteger(args.get(key - 1))) {
                    // integer must be repeat
                    repeat = integer;
                } else {
                    // integer must be delay
                    delay = repeat;
                }
            }
        }
    }

    public TaskBuilder runnable(BTask task) {
        this.task = task;
        return this;
    }

    public TaskBuilder now() {
        this.taskType = TaskType.NOW;
        return this;
    }

    public TaskBuilder aSync() {
        this.aSync = true;
        return this;
    }

    public TaskBuilder sync() {
        this.aSync = false;
        return this;
    }

    public TaskBuilder delay() {
        this.taskType = TaskType.DELAY;
        return this;
    }

    public TaskBuilder repeat() {
        this.taskType = TaskType.REPEAT;
        return this;
    }

    public TaskBuilder taskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public TaskBuilder unit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public TaskBuilder delay(Integer delay) {
        this.delay = delay;
        return this;
    }

    public TaskBuilder repeat(Integer repeat) {
        this.repeat = repeat;
        return this;
    }

    public ATask build() {
        return new ATask() {

            @Override
            public BTask task() {
                return task;
            }

            @Override
            public Boolean aSync() {
                return aSync;
            }

            @Override
            public TaskType taskType() {
                return taskType;
            }

            @Override
            public TimeUnit time() {
                return unit;
            }

            @Override
            public Integer delay() {
                return delay;
            }

            @Override
            public Integer repeat() {
                return repeat;
            }

        };
    }
}