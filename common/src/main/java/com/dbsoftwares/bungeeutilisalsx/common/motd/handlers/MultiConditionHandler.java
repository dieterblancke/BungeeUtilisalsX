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

package com.dbsoftwares.bungeeutilisalsx.common.motd.handlers;

import com.dbsoftwares.bungeeutilisalsx.common.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode( callSuper = true )
public class MultiConditionHandler extends ConditionHandler
{

    private final boolean and;
    private final ConditionHandler[] handlers;

    public MultiConditionHandler( final String condition, final boolean and, final ConditionHandler[] handlers )
    {
        super( condition );

        this.and = and;
        this.handlers = handlers;
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        if ( and )
        {
            for ( ConditionHandler handler : handlers )
            {
                if ( !handler.checkCondition( connection ) )
                {
                    return false;
                }
            }
        }
        else
        {
            for ( ConditionHandler handler : handlers )
            {
                if ( handler.checkCondition( connection ) )
                {
                    return true;
                }
            }
        }
        // defaulting to false
        return false;
    }
}
