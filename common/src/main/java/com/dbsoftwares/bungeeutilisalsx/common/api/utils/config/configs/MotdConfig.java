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

package com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.configs;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.Config;
import com.dbsoftwares.bungeeutilisalsx.common.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdData;
import com.dbsoftwares.bungeeutilisalsx.common.motd.handlers.DomainConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.handlers.MultiConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.handlers.NameConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.handlers.VersionConditionHandler;
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
            final ConditionHandler handler = this.createConditionHandler( condition );

            this.motds.add( new MotdData( handler, handler == null, motd, hoverMessages ) );
        }
    }

    private ConditionHandler createConditionHandler( final String condition )
    {
        if ( condition.contains( "||" ) || condition.contains( "&&" ) )
        {
            return this.createMultiConditionHandler( condition );
        }

        if ( condition.toLowerCase().startsWith( "domain" ) )
        {
            return new DomainConditionHandler( condition );
        }
        else if ( condition.toLowerCase().startsWith( "version" ) )
        {
            return new VersionConditionHandler( condition );
        }
        else if ( condition.toLowerCase().startsWith( "name" ) )
        {
            return new NameConditionHandler( condition );
        }
        else
        {
            return null;
        }
    }

    private ConditionHandler createMultiConditionHandler( final String conditions )
    {
        final List<ConditionHandler> handlers = Lists.newArrayList();

        if ( conditions.contains( "||" ) )
        {
            for ( String condition : conditions.split( "\\|\\|" ) )
            {
                handlers.add( this.createConditionHandler( condition.trim() ) );
            }
        }
        else if ( conditions.contains( "&&" ) )
        {
            for ( String condition : conditions.split( "&&" ) )
            {
                handlers.add( this.createConditionHandler( condition.trim() ) );
            }
        }

        return new MultiConditionHandler( conditions, conditions.contains( "&&" ), handlers.toArray( new ConditionHandler[0] ) );
    }
}
