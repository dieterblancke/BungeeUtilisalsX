package com.dbsoftwares.bungeeutilisals.bungee.event;

import com.dbsoftwares.bungeeutilisals.api.event.event.BUEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class BEventHandler<T extends BUEvent> implements EventHandler<T> {

    private final EventLoader eventloader;
    @Getter private final Class<T> eventClass;
    @Getter
    private final Method executor;
    @Getter
    private final Object executorInstance;
    private final boolean executeIfCancelled;
    @Getter
    private final int priority;

    private final AtomicInteger uses = new AtomicInteger(0);

    @Override
    public void unregister() {
        eventloader.unregister(this);
    }

    @Override
    public int getUsedAmount() {
        return uses.get();
    }

    @Override
    public boolean executeIfCancelled() {
        return executeIfCancelled;
    }

    @SuppressWarnings("unchecked")
    void handle(BUEvent event) {
        try {
            T castedEvent = (T) event;

            executor.invoke(executorInstance, castedEvent);
            uses.getAndIncrement();
        } catch (Throwable t) {
            BungeeUtilisals.log("Could not handle event in " + executor.getClass().getName() + ": " + eventClass.getSimpleName());
            t.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "EventHandler for " + eventClass.getSimpleName() + ". Used Amount: " + uses + ", ingore cancelled: " + executeIfCancelled + ", priority: " + priority + ".";
    }
}