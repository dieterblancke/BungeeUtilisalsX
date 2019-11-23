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

package com.dbsoftwares.bungeeutilisals.importer;

import com.dbsoftwares.bungeeutilisals.utils.ImportUtils;
import com.dbsoftwares.bungeeutilisals.utils.MojangUtils;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
public abstract class Importer
{

    protected final LoadingCache<String, String> uuidCache = CacheBuilder.newBuilder().maximumSize( 15000 )
            .expireAfterAccess( 30, TimeUnit.MINUTES ).build( new CacheLoader<String, String>()
            {
                public String load( @Nullable final String name ) throws IllegalStateException
                {
                    if ( ProxyServer.getInstance().getConfig().isOnlineMode() )
                    {
                        String uuid = MojangUtils.getUuid( name );
                        if ( uuid != null )
                        {
                            return uuid;
                        }
                        else
                        {
                            throw new IllegalStateException( "Could not retrieve uuid of " + name );
                        }
                    }
                    else
                    {
                        return UUID.nameUUIDFromBytes( ("OfflinePlayer:" + name).getBytes( Charsets.UTF_8 ) )
                                .toString().replaceAll( "-", "" );
                    }
                }
            } );
    protected final LoadingCache<String, String> nameCache = CacheBuilder.newBuilder().maximumSize( 15000 )
            .expireAfterAccess( 30, TimeUnit.MINUTES ).build( new CacheLoader<String, String>()
            {
                public String load( @Nullable final String uuid ) throws IllegalStateException
                {
                    String name = MojangUtils.getName( UUID.fromString( uuid ) );
                    if ( name != null )
                    {
                        return name;
                    }
                    else
                    {
                        throw new IllegalStateException( "Could not retrieve name of " + uuid );
                    }
                }
            } );
    private final ImportUtils importUtils = new ImportUtils();
    protected ImporterStatus status;

    protected abstract void importData( final ImporterCallback<ImporterStatus> importerCallback, final Map<String, String> properties ) throws Exception;

    public void startImport( final ImporterCallback<ImporterStatus> importerCallback, final Map<String, String> properties )
    {
        try
        {
            importData( importerCallback, properties );
        }
        catch ( final Throwable t )
        {
            importerCallback.done( null, t );
        }
    }

    protected UUID readUUIDFromString( String str )
    {
        try
        {
            return UUID.fromString( str );
        }
        catch ( IllegalArgumentException e )
        {
            return UUID.fromString(
                    str.replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                            "$1-$2-$3-$4-$5"
                    )
            );
        }
    }

    @Data
    public class ImporterStatus
    {

        private final int totalEntries;
        private int convertedEntries;

        public ImporterStatus( final int totalEntries )
        {
            if ( totalEntries < 1 )
            {
                throw new IllegalArgumentException( "There is no entry to import." );
            }
            this.totalEntries = totalEntries;
            convertedEntries = 0;
        }

        public int incrementConvertedEntries( final int incrementValue )
        {
            return convertedEntries = convertedEntries + incrementValue;
        }

        public double getProgressionPercent()
        {
            return (((double) convertedEntries / (double) totalEntries) * 100);
        }

        public int getRemainingEntries()
        {
            return totalEntries - convertedEntries;
        }
    }
}