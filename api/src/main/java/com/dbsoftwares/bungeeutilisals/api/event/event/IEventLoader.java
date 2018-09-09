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

import lombok.NonNull;

import java.util.Set;

public interface IEventLoader {

    /**
     * Registers a eventhandler to execute stuff.
     *
     * @param clazz The event class.
     * @param executor The EventExecutor
     * @param <T> The event class.
     * @return An eventexecutor instance.
     */
    <T extends BUEvent> Set<EventHandler<T>> register(@NonNull Class<T> clazz, @NonNull EventExecutor executor);

    /**
     * Unregisters a certain EventHandler.
     * @param eventHandler The EventHandler you want to unregister.
     */
    void unregister(EventHandler<?> eventHandler);

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass The event to find handlers for.
     * @param <T> The event class.
     * @return An immutable set of event handlers.
     */
    <T extends BUEvent> Set<EventHandler<T>> getHandlers(Class<T> eventClass);

    /**
     * Gets a set of all registered handlers for all events
     * @return an immutable set of event handlers
     */
    Set<EventHandler> getHandlers();

    /**
     * Launches the given event.
     * @param event The event you want to start.
     */
    void launchEvent(BUEvent event);

    /**
     * Launches the given event async.
     * @param event The event you want to start async.
     */
    void launchEventAsync(BUEvent event);
}