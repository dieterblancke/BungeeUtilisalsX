package com.dbsoftwares.bungeeutilisals.api.event.interfaces;

public interface EventHandler<T extends BUEvent> {

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
     * @return event executor handling the event
     */
    EventExecutor<T> getExecutor();

    /**
     * Gets the amount this event handler has been called
     * @return the amount of times this handler has been called
     */
    int getUsedAmount();
}