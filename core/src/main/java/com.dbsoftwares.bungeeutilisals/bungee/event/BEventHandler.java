package com.dbsoftwares.bungeeutilisals.bungee.event;

import com.dbsoftwares.bungeeutilisals.api.event.BUEvent;
import com.dbsoftwares.bungeeutilisals.api.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class BEventHandler<T extends BUEvent> implements EventHandler<T> {

    private final EventLoader eventloader;
    @Getter private final Class<T> eventClass;
    @Getter private final EventExecutor<T> executor;
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final AtomicInteger callCount = new AtomicInteger(0);

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public boolean unregister() {
        // already unregistered
        if (!active.getAndSet(false)) {
            return false;
        }

        eventloader.unregister(this);
        return true;
    }

    @Override
    public int getExecutedAmount() {
        return callCount.get();
    }

    @SuppressWarnings("unchecked")
    void handle(BUEvent event) {
        try {
            T t = (T) event;
            executor.onExecute(t);
            callCount.incrementAndGet();
        } catch (Throwable t) {
            BungeeUtilisals.log("Unable to pass event " + event.getClass().getSimpleName() + " to handler " + executor.getClass().getName());
            t.printStackTrace();
        }
    }
}
