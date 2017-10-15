package com.dbsoftwares.bungeeutilisals.api.event;

import javax.annotation.Nonnull;

public interface EventHandler<T extends BUEvent> {

    /**
     * Gets the class this handler is listening to
     *
     * @return the event class
     */
    @Nonnull Class<T> getEventClass();

    /**
     * Returns true if this handler is active
     *
     * @return true if this handler is still active
     */
    boolean isActive();

    /**
     * Unregisters this handler from the event bus
     *
     * @return true if the handler wasn't already unregistered
     */
    boolean unregister();

    /**
     * Gets the event executor responsible for handling the event
     *
     * @return the event executor
     */
    @Nonnull EventExecutor<T> getExecutor();

    /**
     * Gets the number of times this handler has been called
     *
     * @return the number of times this handler has been called
     */
    int getCallCount();

}