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

package be.dieterblancke.bungeeutilisalsx.common.storage.hikari;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.SQLDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.sql.SQLStorageManager;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

public abstract class HikariStorageManager extends SQLStorageManager
{

    @Getter
    protected HikariConfig config;

    @Getter
    protected HikariDataSource dataSource;

    HikariStorageManager( StorageType type, IConfiguration configuration, HikariConfig cfg )
    {
        super( type, new SQLDao() );
        config = cfg == null ? new HikariConfig() : cfg;
        if ( getDataSourceClass() != null )
        {
            config.setDataSourceClassName( getDataSourceClass() );
            config.addDataSourceProperty( "serverName", configuration.getString( "storage.hostname" ) );
            config.addDataSourceProperty( "port" + ( type.equals( StorageType.POSTGRESQL ) ? "Number" : "" ),
                    configuration.getInteger( "storage.port" ) );
            config.addDataSourceProperty( "databaseName", configuration.getString( "storage.database" ) );
        }
        config.setUsername( configuration.getString( "storage.username" ) );
        config.setPassword( configuration.getString( "storage.password" ) );

        final ISection propertySection = configuration.getSection( "storage.properties" );
        for ( String key : propertySection.getKeys() )
        {
            if ( config.getDataSourceClassName() == null )
            {
                continue;
            }
            try
            {
                final Class<?> clazz = Class.forName( config.getDataSourceClassName() );

                if ( this.hasProperty( clazz, key ) )
                {
                    config.addDataSourceProperty( key, propertySection.get( key ) );
                }
            }
            catch ( ClassNotFoundException e )
            {
                // continue
            }
        }

        config.setMaximumPoolSize( configuration.getInteger( "storage.pool.max-pool-size" ) );
        config.setMinimumIdle( configuration.getInteger( "storage.pool.min-idle" ) );
        config.setMaxLifetime( configuration.getInteger( "storage.pool.max-lifetime" ) * 1000 );
        config.setConnectionTimeout( configuration.getInteger( "storage.pool.connection-timeout" ) * 1000 );

        config.setPoolName( "BungeeUtilisalsX" );
        config.setLeakDetectionThreshold( 10000 );
        config.setConnectionTestQuery( "/* BungeeUtilisalsX ping */ SELECT 1;" );

        try
        {
            config.setInitializationFailTimeout( -1 );
        }
        catch ( NoSuchMethodError e )
        {
            // do nothing
        }

        dataSource = new HikariDataSource( config );
    }

    private boolean hasProperty( final Class<?> clazz, final String key )
    {
        final String methodName = "set" + key.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + key.substring( 1 );

        return Arrays.stream( clazz.getDeclaredMethods() )
                .anyMatch( method -> method.getName().equalsIgnoreCase( methodName ) );
    }

    protected abstract String getDataSourceClass();

    @Override
    public void close()
    {
        dataSource.close();
    }
}
