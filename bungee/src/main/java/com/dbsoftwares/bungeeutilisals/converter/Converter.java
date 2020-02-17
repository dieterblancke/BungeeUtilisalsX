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

package com.dbsoftwares.bungeeutilisals.converter;

import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.utils.ImportUtils;
import lombok.Data;

import java.util.Map;

@Data
public abstract class Converter
{

    private final ImportUtils importUtils = new ImportUtils();
    protected ConverterStatus status;

    protected abstract void importData( final ImporterCallback<ConverterStatus> importerCallback, final Map<String, String> properties ) throws Exception;

    public void startImport( final ImporterCallback<ConverterStatus> importerCallback, final Map<String, String> properties )
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

    @Data
    public class ConverterStatus
    {

        private final long totalEntries;
        private long convertedEntries;

        public ConverterStatus( final long totalEntries )
        {
            if ( totalEntries < 1 )
            {
                throw new IllegalArgumentException( "There is no entry to convert." );
            }
            this.totalEntries = totalEntries;
            convertedEntries = 0;
        }

        public long incrementConvertedEntries( final long incrementValue )
        {
            return convertedEntries = convertedEntries + incrementValue;
        }

        public double getProgressionPercent()
        {
            return (((double) convertedEntries / (double) totalEntries) * 100);
        }

        public long getRemainingEntries()
        {
            return totalEntries - convertedEntries;
        }
    }
}