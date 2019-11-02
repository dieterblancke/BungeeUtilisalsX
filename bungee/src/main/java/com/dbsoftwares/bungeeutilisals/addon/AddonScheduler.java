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

import com.dbsoftwares.bungeeutilisals.api.addon.Addon;
import com.dbsoftwares.bungeeutilisals.api.addon.IAddonTask;
import com.dbsoftwares.bungeeutilisals.api.addon.IScheduler;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;

public class AddonScheduler implements IScheduler
{

    private final Object lock = new Object();
    private final Multimap<String, IAddonTask> scheduledTasks = Multimaps.synchronizedMultimap( HashMultimap.create() );

    @Override
    public void registerTask( final Addon addon, final IAddonTask task )
    {
        this.scheduledTasks.put( addon.getDescription().getName(), task );
    }

    @Override
    public Collection<IAddonTask> getTasks( final String addonName )
    {
        return scheduledTasks.get( addonName );
    }

    @Override
    public void cancel( final Addon addon )
    {
        getTasks( addon.getDescription().getName() ).forEach( IAddonTask::cancel );
    }

    protected void cancel( AddonTask task )
    {
        synchronized ( lock )
        {
            scheduledTasks.values().remove( task );
        }
    }

    @Override
    public IAddonTask runAsync( final Addon addon, final Runnable runnable )
    {
        return schedule( addon, runnable, 0, TimeUnit.MILLISECONDS );
    }

    @Override
    public IAddonTask schedule( final Addon addon, final Runnable runnable, final long delay, final TimeUnit unit )
    {
        return schedule( addon, runnable, delay, 0, unit );
    }

    @Override
    @SuppressWarnings("deprecation")
    public IAddonTask schedule( final Addon addon, final Runnable runnable, final long delay, final long repeat, final TimeUnit unit )
    {
        Validate.checkNotNull( addon, "Addon cannot be null." );
        Validate.checkNotNull( runnable, "Owner cannot be null." );
        AddonTask prepared = new AddonTask( this, addon, runnable, delay, repeat, unit );

        synchronized ( lock )
        {
            scheduledTasks.put( addon.getDescription().getName(), prepared );
        }

        addon.getExecutorService().execute( prepared );
        return prepared;
    }
}
