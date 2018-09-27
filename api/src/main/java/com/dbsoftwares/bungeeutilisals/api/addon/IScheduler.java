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
