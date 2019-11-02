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

package com.dbsoftwares.bungeeutilisals.api.event.event;

import java.lang.reflect.Method;

public interface EventHandler<T extends BUEvent>
{

    /**
     * @return the event clas this handler listens to.
     */
    Class<T> getEventClass();

    /**
     * Stops this handler from listening to events.
     */
    void unregister();

    /**
     * Gets the event executor handling the event
     *
     * @return event executor handling the event
     */
    Method getExecutor();

    /**
     * Gets the amount this event handler has been called
     *
     * @return the amount of times this handler has been called
     */
    int getUsedAmount();

    /**
     * Gets whether the event should be executed when cancelled or not.
     *
     * @return True if it should ignore cancelled events, false if not.
     */
    boolean executeIfCancelled();

    /**
     * Gets the event priority. The lower the priority is, the earlier it will get executed.
     * Higher priority = later executed = last influence
     *
     * @return the priority of this event
     */
    int getPriority();
}