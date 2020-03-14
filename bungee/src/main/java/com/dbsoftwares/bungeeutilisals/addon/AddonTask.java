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

package com.dbsoftwares.bungeeutilisals.addon;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.Addon;
import com.dbsoftwares.bungeeutilisals.api.addon.IAddonTask;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

@Data
public class AddonTask implements Runnable, IAddonTask
{

    private static AtomicInteger identifiers = new AtomicInteger( 0 );

    private final AddonScheduler scheduler;
    private final int id;
    private final Addon owner;
    private final Runnable task;
    private final long delay;
    private final long period;
    private final AtomicBoolean running = new AtomicBoolean( true );

    public AddonTask( AddonScheduler scheduler, Addon owner, Runnable task, long delay, long period, TimeUnit unit )
    {
        this.scheduler = scheduler;
        this.id = identifiers.incrementAndGet();
        this.owner = owner;
        this.task = task;
        this.delay = unit.toMillis( delay );
        this.period = unit.toMillis( period );
    }

    @Override
    public void cancel()
    {
        boolean wasRunning = running.getAndSet( false );

        if ( wasRunning )
        {
            scheduler.cancel( this );
        }
    }

    @Override
    public void run()
    {
        if ( delay > 0 )
        {
            try
            {
                Thread.sleep( delay );
            }
            catch ( InterruptedException ex )
            {
                Thread.currentThread().interrupt();
            }
        }

        while ( running.get() )
        {
            try
            {
                task.run();
            }
            catch ( Exception e )
            {
                BUCore.getLogger().log( Level.SEVERE, String.format( "Task %s encountered an exception", id ), e );
            }

            if ( period <= 0 )
            {
                break;
            }

            try
            {
                Thread.sleep( period );
            }
            catch ( InterruptedException ex )
            {
                Thread.currentThread().interrupt();
            }
        }

        cancel();
    }
}