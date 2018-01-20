package com.dbsoftwares.bungeeutilisals.bungee.event;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventLoader implements IEventLoader {

    private final Map<Class<? extends BUEvent>, Set<EventHandler<?>>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public <T extends BUEvent> EventHandler<T> register(Class<T> eventClass, EventExecutor<T> handler) {
        if (!BUEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("class " + eventClass.getName() + " does not implement BUEvent");
        }
        Set<EventHandler<?>> handlers = handlerMap.computeIfAbsent(eventClass, c -> ConcurrentHashMap.newKeySet());

        BEventHandler<T> eventHandler = new BEventHandler<>(this, eventClass, handler);
        handlers.add(eventHandler);
        return eventHandler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BUEvent> Set<EventHandler<T>> getHandlers(Class<T> eventClass) {
        Set<EventHandler<?>> handlers = handlerMap.get(eventClass);
        if (handlers == null) {
            return ImmutableSet.of();
        } else {
            ImmutableSet.Builder<EventHandler<T>> ret = ImmutableSet.builder();
            for (EventHandler<?> handler : handlers) {
                ret.add((EventHandler<T>) handler);
            }

            return ret.build();
        }
    }

    @Override
    public Set<EventHandler> getHandlers() {
        Set<EventHandler> handlers = Sets.newHashSet();
        handlerMap.values().forEach(handlers::addAll);
        return handlers;
    }

    @Override
    public void unregister(EventHandler<?> handler) {
        Set<EventHandler<?>> handlers = handlerMap.get(handler.getEventClass());
        if (handlers != null && handlers.contains(handler)) {
            handlers.remove(handler);
        }
    }

    @Override
    public void launchEvent(BUEvent event) {
        if (event instanceof AbstractEvent) {
            ((AbstractEvent) event).setApi(BUCore.getApi());
        }

        for (Map.Entry<Class<? extends BUEvent>, Set<EventHandler<?>>> ent : handlerMap.entrySet()) {
            if (!ent.getKey().isAssignableFrom(event.getClass())) {
                continue;
            }

            ent.getValue().forEach(h -> {
                if (h instanceof BEventHandler) {
                    BEventHandler handler = (BEventHandler) h;
                    handler.handle(event);
                }
            });
        }
    }

    @Override
    public void launchEventAsync(BUEvent event) {
        if (event instanceof Cancellable) {
            throw new IllegalArgumentException("cannot call Cancellable event async");
        }
        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> launchEvent(event));
    }
}
