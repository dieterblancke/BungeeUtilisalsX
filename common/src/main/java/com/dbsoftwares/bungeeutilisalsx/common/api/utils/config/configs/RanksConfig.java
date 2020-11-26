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
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class RanksConfig extends Config
{

    @Getter
    private final List<StaffRankData> ranks = Lists.newArrayList();

    public RanksConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        ranks.clear();
    }

    @Override
    public void setup()
    {
        final List<ISection> sections = config.getSectionList( "ranks" );

        for ( ISection section : sections )
        {
            final String name = section.getString( "name" );
            final String display = section.getString( "display" );
            final String permission = section.getString( "permission" );
            final int priority = section.getInteger( "priority" );

            ranks.add( new StaffRankData( name, display, permission, priority ) );
        }
    }
}
