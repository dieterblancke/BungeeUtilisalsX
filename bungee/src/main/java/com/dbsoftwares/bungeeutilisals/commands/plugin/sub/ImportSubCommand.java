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
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.importer.Importer;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.importer.importers.BungeeAdminToolsImporter;
import com.dbsoftwares.bungeeutilisals.importer.importers.BungeeUtilisalsImporter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ImportSubCommand extends SubCommand
{

    public ImportSubCommand()
    {
        super( "import" );
    }

    @Override
    public String getUsage()
    {
        return "/bungeeutilisals import (plugin) [properties]";
    }

    @Override
    public String getPermission()
    {
        return "bungeeutilisals.admin.import";
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length == 1 || args.length == 2 )
        {
            final String plugin = args[0];
            final Map<String, String> properties = Maps.newHashMap();

            if ( args.length == 2 )
            {
                for ( String property : args[1].split( "," ) )
                {
                    properties.put( property.split( ":" )[0], property.split( ":" )[1] );
                }
            }

            Importer importer = null;
            if ( plugin.equalsIgnoreCase( "BungeeUtilisals" ) )
            {
                importer = new BungeeUtilisalsImporter();
            }
            else if ( plugin.equalsIgnoreCase( "BungeeAdminTools" ) || plugin.equalsIgnoreCase( "BAT" ) )
            {
                importer = new BungeeAdminToolsImporter();
            }

            if ( importer != null )
            {
                importer.startImport( new ImporterCallback<Importer.ImporterStatus>()
                {
                    @Override
                    public void onStatusUpdate( Importer.ImporterStatus status )
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
                    public void done( Importer.ImporterStatus status, Throwable throwable )
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
    }

    @Override
    public List<String> getCompletions( User user, String[] args )
    {
        return ImmutableList.of();
    }
}
