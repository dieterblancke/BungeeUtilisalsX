package com.dbsoftwares.bungeeutilisals.api.event;

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
    <T extends BUEvent> EventHandler<T> register(@NonNull Class<T> clazz, @NonNull EventExecutor<T> executor);

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