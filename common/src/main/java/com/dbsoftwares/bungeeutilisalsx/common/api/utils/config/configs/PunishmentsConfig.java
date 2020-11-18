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

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.Config;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;

public class PunishmentsConfig extends Config
{

    @Getter
    private final Map<PunishmentType, List<PunishmentAction>> punishmentActions = Maps.newHashMap();

    public PunishmentsConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        punishmentActions.clear();
    }

    @Override
    public void setup()
    {
        for ( ISection section : config.getSectionList( "actions" ) )
        {
            try
            {
                final String uid = section.getString( "uid" );
                final PunishmentType type = PunishmentType.valueOf( section.getString( "type" ) );

                try
                {
                    final TimeUnit unit = TimeUnit.valueOf( section.getString( "time.unit" ) );

                    if ( section.isInteger( "time.amount" ) )
                    {
                        final int amount = section.getInteger( "time.amount" );
                        final int limit = section.getInteger( "limit" );

                        final PunishmentAction action = new PunishmentAction( uid, type, unit, amount, limit, section.getStringList( "actions" ) );
                        final List<PunishmentAction> actions = punishmentActions.getOrDefault( type, Lists.newArrayList() );

                        actions.add( action );
                        punishmentActions.put( type, actions );
                    }
                    else
                    {
                        BuX.getLogger().warning( "An invalid number has been entered (" + section.getString( "time.amount" ) + ")." );
                    }
                }
                catch ( IllegalArgumentException e )
                {
                    BuX.getLogger().warning( "An invalid time unit has been entered (" + section.getString( "time.unit" ) + ")." );
                }
            }
            catch ( IllegalArgumentException e )
            {
                BuX.getLogger().warning( "An invalid punishment type has been entered (" + section.getString( "type" ) + ")." );
            }
        }
    }
}
