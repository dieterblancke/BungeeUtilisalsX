package com.dbsoftwares.bungeeutilisals.bungee.event;

import com.dbsoftwares.bungeeutilisals.api.event.interfaces.BUEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.EventHandler;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class BEventHandler<T extends BUEvent> implements EventHandler<T> {

    private final EventLoader eventloader;
    @Getter private final Class<T> eventClass;
    @Getter private final EventExecutor<T> executor;
    private final AtomicInteger uses = new AtomicInteger(0);

    @Override
    public void unregister() {
        eventloader.unregister(this);
    }

    @Override
    public int getUsedAmount() {
        return uses.get();
    }

    @SuppressWarnings("unchecked")
    void handle(BUEvent event) {
        try {
            T castedEvent = (T) event;
            executor.onExecute(castedEvent);
            uses.getAndIncrement();
        } catch (Throwable t) {
            BungeeUtilisals.log("Could not handle event in " + executor.getClass().getName() + ": " + eventClass.getSimpleName());
            t.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "EventHandler for " + eventClass.getSimpleName() + ". Used Amount: " + uses;
    }
}
