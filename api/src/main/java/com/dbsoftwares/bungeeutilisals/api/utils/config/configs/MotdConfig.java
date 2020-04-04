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

package com.dbsoftwares.bungeeutilisals.api.utils.config.configs;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.motd.MotdData;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.DomainConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.NameConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.VersionConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.config.Config;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class MotdConfig extends Config
{
    @Getter
    private final List<MotdData> motds = Lists.newArrayList();

    public MotdConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        motds.clear();
    }

    @Override
    protected void setup()
    {
        if ( config == null )
        {
            return;
        }

        for ( ISection section : config.getSectionList( "motd" ) )
        {
            final String condition = section.getString( "condition" );
            final String motd = section.getString( "motd" );
            final List<String> hoverMessages = section.exists( "player-hover" )
                    ? section.getStringList( "player-hover" )
                    : Lists.newArrayList();

            if ( condition.equalsIgnoreCase( "default" ) )
            {
                motds.add( new MotdData( null, true, motd, hoverMessages ) );
            }
            else if ( condition.toLowerCase().startsWith( "domain" ) )
            {
                motds.add( new MotdData( new DomainConditionHandler( condition ), false, motd, hoverMessages ) );
            }
            else if ( condition.toLowerCase().startsWith( "version" ) )
            {
                motds.add( new MotdData( new VersionConditionHandler( condition ), false, motd, hoverMessages ) );
            }
            else if ( condition.toLowerCase().startsWith( "name" ) )
            {
                motds.add( new MotdData( new NameConditionHandler( condition ), false, motd, hoverMessages ) );
            }
            else
            {
                BUCore.getLogger().warning( "An invalid MOTD condition has been entered." );
                BUCore.getLogger().warning( "For all available conditions, see https://docs.dbsoftwares.eu/bungeeutilisals/motd-chat#conditions" );
            }
        }
    }
}
