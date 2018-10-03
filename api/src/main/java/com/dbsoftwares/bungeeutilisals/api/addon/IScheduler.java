/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.addon;

import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;

import java.util.Collection;

public interface IScheduler {

    void registerTask(final Addon addon, final IAddonTask task);

    Collection<IAddonTask> getTasks(final String addonName);

    void cancel(final Addon addon);

    IAddonTask runAsync(final Addon addon, final Runnable runnable);

    IAddonTask schedule(final Addon addon, final Runnable runnable, final long delay, final TimeUnit unit);

    IAddonTask schedule(final Addon addon, final Runnable runnable, final long delay, final long repeat, final TimeUnit unit);

}
