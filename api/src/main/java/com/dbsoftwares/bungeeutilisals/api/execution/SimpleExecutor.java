/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

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