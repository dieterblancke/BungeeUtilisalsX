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

package com.dbsoftwares.bungeeutilisals.updater;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Sets;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
public class Updater
{

    private static Set<Updater> updaters = Sets.newConcurrentHashSet();
    private UpdatableData updatable;
    private ScheduledTask task;

    public Updater( final UpdatableData updatable )
    {
        updaters.add( this );

        this.updatable = updatable;
        initialize();
    }

    public static Updater initialize( final Object instance )
    {
        final Class<?> clazz = instance.getClass();

        if ( !clazz.isAnnotationPresent( Updatable.class ) )
        {
            throw new RuntimeException( clazz.getSimpleName() + " does not have the @Updatable annotation." );
        }

        try
        {
            final Updatable updatable = clazz.getAnnotation( Updatable.class );
            final Method getDescription = ReflectionUtils.getMethod( clazz, "getDescription" );
            final Object description = getDescription.invoke( instance );

            final Method getName = ReflectionUtils.getMethod( description.getClass(), "getName" );
            final Method getVersion = ReflectionUtils.getMethod( description.getClass(), "getVersion" );
            final Method getFile = ReflectionUtils.getMethod( description.getClass(), "getFile" );

            final String url = updatable.url();
            final String name = (String) getName.invoke( description );
            final String version = (String) getVersion.invoke( description );
            final File file = (File) getFile.invoke( description );

            // Initialize updater info
            final UpdatableData data = new UpdatableData( name, version, url, file );

            return new Updater( data );
        }
        catch ( IllegalAccessException | InvocationTargetException e )
        {
            throw new RuntimeException( "Could not initialize Updatable for " + clazz.getSimpleName() + "." );
        }
    }

    public static void shutdownUpdaters()
    {
        updaters.forEach( Updater::shutdown );
    }

    private void initialize()
    {
        final IConfiguration config = ConfigFiles.CONFIG.getConfig();
        final int delay = config.getInteger( "updater.delay" );

        if ( delay <= 0 )
        {
            task = ProxyServer.getInstance().getScheduler().runAsync( BUCore.getApi().getPlugin(), new UpdateRunner( this ) );
        }
        else
        {
            task = ProxyServer.getInstance().getScheduler().schedule( BUCore.getApi().getPlugin(), new UpdateRunner( this ), 0, delay, TimeUnit.MINUTES );
        }
    }

    public void shutdown()
    {
        if ( !ConfigFiles.CONFIG.getConfig().getBoolean( "updater.install" ) )
        {
            return;
        }

        ((UpdateRunner) task.getTask()).shutdown();
        task.cancel();
    }

    private boolean shouldInstall()
    {
        return ConfigFiles.CONFIG.getConfig().getBoolean( "updater.install" );
    }
}