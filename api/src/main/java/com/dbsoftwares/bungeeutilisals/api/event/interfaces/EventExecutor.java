package com.dbsoftwares.bungeeutilisals.api.event.interfaces;

public interface EventExecutor<T> {

    /**
     * Being executed by an EventHandler listening to a certain event.
     *
     * @param event The event instance which has been launched.
     */
    void onExecute(T event);

}