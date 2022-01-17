/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.event;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.*;
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
    public <T extends BUEvent> Set<IEventHandler<T>> register( Class<T> eventClass, EventExecutor executor )
    {
        if ( !BUEvent.class.isAssignableFrom( eventClass ) )
        {
            throw new IllegalArgumentException( "class " + eventClass.getName() + " does not implement BUEvent" );
        }
        Set<IEventHandler> handlers = handlerMap.computeIfAbsent( eventClass, c -> ConcurrentHashMap.newKeySet() );
        Set<IEventHandler<T>> addedHandlers = ConcurrentHashMap.newKeySet();

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

                EventHandler<T> eventHandler = new EventHandler<>( this, eventClass, method, executor, executeIfCancelled, priority );
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
                if ( h instanceof EventHandler )
                {
                    EventHandler handler = (EventHandler) h;

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
