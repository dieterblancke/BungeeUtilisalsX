package com.dbsoftwares.bungeeutilisals.bungee.event;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventLoader implements IEventLoader {

    private final Map<Class<? extends BUEvent>, Set<EventHandler<?>>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public <T extends BUEvent> Set<EventHandler<T>> register(Class<T> eventClass, EventExecutor executor) {
        if (!BUEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException("class " + eventClass.getName() + " does not implement BUEvent");
        }
        Set<EventHandler<?>> handlers = handlerMap.computeIfAbsent(eventClass, c -> ConcurrentHashMap.newKeySet());
        Set<EventHandler<T>> addedHandlers = ConcurrentHashMap.newKeySet();

        for (Method method : executor.getClass().getDeclaredMethods()) {
            if (method.getParameters()[0].getType().equals(eventClass) && method.isAnnotationPresent(Event.class)) {
                Event event = method.getAnnotation(Event.class);
                int priority = event.priority();
                boolean executeIfCancelled = event.executeIfCancelled();

                method.setAccessible(true);

                BEventHandler<T> eventHandler = new BEventHandler<>(this, eventClass, method, executor, executeIfCancelled, priority);

                System.out.println(eventHandler.toString());

                handlers.add(eventHandler);
                addedHandlers.add(eventHandler);
            }
        }

        return addedHandlers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BUEvent> Set<EventHandler<T>> getHandlers(Class<T> eventClass) {
        Set<EventHandler<?>> handlers = handlerMap.get(eventClass);
        if (handlers == null) {
            return ImmutableSet.of();
        } else {
            ImmutableSet.Builder<EventHandler<T>> builder = ImmutableSet.builder();

            handlers.forEach(handler -> builder.add((EventHandler<T>) handler));

            return builder.build();
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

            List<EventHandler<?>> sortedSet = Lists.newArrayList(ent.getValue());
            sortedSet.sort(Comparator.comparingInt(EventHandler::getPriority));

            for (EventHandler h : sortedSet) {
                if (h instanceof BEventHandler) {
                    BEventHandler handler = (BEventHandler) h;

                    if (!handler.executeIfCancelled() && event instanceof Cancellable && ((Cancellable) event).isCancelled()) {
                        continue;
                    }
                    handler.handle(event);
                }
            }
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
