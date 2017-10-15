package com.dbsoftwares.bungeeutilisals.api.event;

public interface EventExecutor<T> {

    void onExecute(T event);

}