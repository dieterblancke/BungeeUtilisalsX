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

package com.dbsoftwares.bungeeutilisals.commands.plugin.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.converter.Converter;
import com.dbsoftwares.bungeeutilisals.converter.converters.MongoToMongoConverter;
import com.dbsoftwares.bungeeutilisals.converter.converters.MongoToSQLConverter;
import com.dbsoftwares.bungeeutilisals.converter.converters.SQLtoMongoConverter;
import com.dbsoftwares.bungeeutilisals.converter.converters.SQLtoSQLConverter;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ConvertSubCommandCall implements CommandCall, TabCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 1 || args.size() == 2 )
        {
            final String oldtype = args.get( 0 );
            final Map<String, String> properties = Maps.newHashMap();

            if ( args.size() == 2 )
            {
                for ( String property : args.get( 1 ).split( "," ) )
                {
                    properties.put( property.split( ":" )[0], property.split( ":" )[1] );
                }
            }

            final Converter converter;
            if ( oldtype.toLowerCase().contains( "sql" ) )
            {
                if ( BUCore.getApi().getStorageManager().getType().toString().contains( "SQL" ) )
                {
                    converter = new SQLtoSQLConverter();
                }
                else
                {
                    converter = new SQLtoMongoConverter();
                }
            }
            else
            {
                if ( BUCore.getApi().getStorageManager().getType().toString().contains( "SQL" ) )
                {
                    converter = new MongoToSQLConverter();
                }
                else
                {
                    converter = new MongoToMongoConverter();
                }
            }

            converter.startImport( new ImporterCallback<Converter.ConverterStatus>()
            {
                @Override
                public void onStatusUpdate( Converter.ConverterStatus status )
                {
                    if ( status.getConvertedEntries() % 100 == 0 )
                    {
                        BUCore.getLogger().info(
                                "Converted " + status.getConvertedEntries() + " out of " + status.getTotalEntries()
                                        + " entries (" + MathUtils.formatNumber( status.getProgressionPercent(), 2 ) + " %)"
                        );
                    }
                }

                @Override
                public void done( Converter.ConverterStatus status, Throwable throwable )
                {
                    BUCore.getLogger().info(
                            "Finished converting " + status.getConvertedEntries() + " out of " + status.getTotalEntries()
                                    + ". " + status.getRemainingEntries() + " could not be converted ("
                                    + status.getProgressionPercent() + " %)"
                    );
                }
            }, properties );
        }
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }
}
