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

import java.util.concurrent.ScheduledFuture;

public interface IExecutor
{

    /**
     * Executes a simple job.
     *
     * @param executor The job you want to execute.
     */
    void execute( SimpleJob executor );

    /**
     * Executes a simple job async.
     *
     * @param executor The job you want to execute async.
     */
    void asyncExecute( SimpleJob executor );

    /**
     * Executes an async delayed simple job.
     *
     * @param delay    Time, in seconds, untill execution.
     * @param executor The job you want to execute async delayed.
     */
    void delayedExecute( int delay, SimpleJob executor );

    /**
     * Executes a,n async repeating simple job.
     *
     * @param delay    Time, in seconds, untill execution.
     * @param executor The job you want to execute async delayed.
     * @return an instance of ScheduledFuture
     */
    ScheduledFuture scheduledExecute( int delay, SimpleJob executor );
}