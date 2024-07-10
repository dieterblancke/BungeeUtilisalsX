package dev.endoy.bungeeutilisalsx.common.api.event.event;

import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

public interface IEventLoader
{

    /**
     * Registers a eventhandler to execute stuff.
     *
     * @param clazz    The event class.
     * @param executor The EventExecutor
     * @return An eventexecutor instance.
     */
    Set<IEventHandler<? extends BUEvent>> register( @NonNull EventExecutor executor, @NonNull Class<? extends BUEvent> clazz );

    default Set<IEventHandler<? extends BUEvent>> register( @NonNull EventExecutor executor, @NonNull Class<? extends BUEvent>... classes )
    {
        final Set<IEventHandler<? extends BUEvent>> combinedSet = new HashSet<>();

        for ( Class<? extends BUEvent> clazz : classes )
        {
            combinedSet.addAll( register( executor, clazz ) );
        }

        return combinedSet;
    }


    /**
     * Unregisters a certain EventHandler.
     *
     * @param eventHandler The EventHandler you want to unregister.
     */
    void unregister( IEventHandler<?> eventHandler );

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass The event to find handlers for.
     * @param <T>        The event class.
     * @return An immutable set of event handlers.
     */
    <T extends BUEvent> Set<IEventHandler<T>> getHandlers( Class<T> eventClass );

    /**
     * Gets a set of all registered handlers for all events
     *
     * @return an immutable set of event handlers
     */
    Set<IEventHandler> getHandlers();

    /**
     * Launches the given event.
     *
     * @param event The event you want to start.
     */
    void launchEvent( BUEvent event );

    /**
     * Launches the given event async.
     *
     * @param event The event you want to start async.
     */
    void launchEventAsync( BUEvent event );
}