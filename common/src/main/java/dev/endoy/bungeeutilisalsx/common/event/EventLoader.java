package dev.endoy.bungeeutilisalsx.common.event;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventLoader implements IEventLoader
{

    private final Map<Class<? extends BUEvent>, Set<IEventHandler>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public Set<IEventHandler<? extends BUEvent>> register( EventExecutor executor, Class<? extends BUEvent> eventClass )
    {
        if ( !BUEvent.class.isAssignableFrom( eventClass ) )
        {
            throw new IllegalArgumentException( "class " + eventClass.getName() + " does not implement BUEvent" );
        }
        Set<IEventHandler> handlers = handlerMap.computeIfAbsent( eventClass, c -> ConcurrentHashMap.newKeySet() );
        Set<IEventHandler<? extends BUEvent>> addedHandlers = ConcurrentHashMap.newKeySet();

        final List<Method> methods = Lists.newArrayList();
        methods.addAll( Arrays.asList( executor.getClass().getDeclaredMethods() ) );

        if ( executor.getClass().getSuperclass() != null )
        {
            methods.addAll( Arrays.asList( executor.getClass().getSuperclass().getDeclaredMethods() ) );
        }

        for ( Method method : methods )
        {
            if (
                    method.getParameterCount() > 0
                            && method.getParameters()[0].getType().equals( eventClass )
                            && method.isAnnotationPresent( Event.class )
            )
            {
                Event event = method.getAnnotation( Event.class );
                int priority = event.priority();
                boolean executeIfCancelled = event.executeIfCancelled();

                method.setAccessible( true );

                EventHandler<? extends BUEvent> eventHandler = new EventHandler<>( this, eventClass, method, executor, executeIfCancelled, priority );
                handlers.add( eventHandler );
                addedHandlers.add( eventHandler );
            }
        }

        return addedHandlers;
    }

    @Override
    public <T extends BUEvent> Set<IEventHandler<T>> getHandlers( Class<T> eventClass )
    {
        Set<IEventHandler> handlers = handlerMap.get( eventClass );
        if ( handlers == null )
        {
            return ImmutableSet.of();
        }
        else
        {
            ImmutableSet.Builder<IEventHandler<T>> builder = ImmutableSet.builder();

            handlers.forEach( builder::add );

            return builder.build();
        }
    }

    @Override
    public Set<IEventHandler> getHandlers()
    {
        Set<IEventHandler> handlers = Sets.newHashSet();
        handlerMap.values().forEach( handlers::addAll );
        return handlers;
    }

    @Override
    public void unregister( IEventHandler handler )
    {
        Set<IEventHandler> handlers = handlerMap.get( handler.getEventClass() );
        if ( handlers != null )
        {
            handlers.remove( handler );
        }
    }

    @Override
    public void launchEvent( BUEvent event )
    {
        for ( Map.Entry<Class<? extends BUEvent>, Set<IEventHandler>> ent : handlerMap.entrySet() )
        {
            if ( !ent.getKey().isAssignableFrom( event.getClass() ) )
            {
                continue;
            }

            List<IEventHandler> sortedSet = Lists.newArrayList( ent.getValue() );
            sortedSet.sort( Comparator.comparingInt( IEventHandler::getPriority ) );

            for ( IEventHandler h : sortedSet )
            {
                if ( h instanceof EventHandler handler )
                {

                    if ( !handler.executeIfCancelled() && event instanceof Cancellable && ( (Cancellable) event ).isCancelled() )
                    {
                        continue;
                    }
                    handler.handle( event );
                }
            }
        }

        if ( event instanceof HasCompletionHandlers c )
        {
            c.handleCompletion();
        }
    }

    @Override
    public void launchEventAsync( BUEvent event )
    {
        if ( event instanceof Cancellable )
        {
            throw new IllegalArgumentException( "cannot call Cancellable event async" );
        }
        BuX.getInstance().getScheduler().runAsync( () -> launchEvent( event ) );
    }
}
