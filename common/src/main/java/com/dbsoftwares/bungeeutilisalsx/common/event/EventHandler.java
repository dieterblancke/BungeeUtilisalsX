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

package com.dbsoftwares.bungeeutilisalsx.common.event;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.BUEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@RequiredArgsConstructor
public class EventHandler<T extends BUEvent> implements IEventHandler<T>
{

    private final EventLoader eventloader;
    @Getter
    private final Class<T> eventClass;
    @Getter
    private final Method executor;
    @Getter
    private final Object executorInstance;
    private final boolean executeIfCancelled;
    @Getter
    private final int priority;

    private final AtomicInteger uses = new AtomicInteger( 0 );

    @Override
    public void unregister()
    {
        eventloader.unregister( this );
    }

    @Override
    public int getUsedAmount()
    {
        return uses.get();
    }

    @Override
    public boolean executeIfCancelled()
    {
        return executeIfCancelled;
    }

    void handle( BUEvent event )
    {
        try
        {
            T castedEvent = (T) event;

            executor.invoke( executorInstance, castedEvent );
            uses.getAndIncrement();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log(
                    Level.SEVERE,
                    "Could not handle event in " + executor.getClass().getName() + ": " + eventClass.getSimpleName() + ": ",
                    e
            );
        }
    }

    @Override
    public String toString()
    {
        return "EventHandler for " + eventClass.getSimpleName() + ". Used Amount: " + uses + ", ingore cancelled: " + executeIfCancelled + ", priority: " + priority + ".";
    }
}