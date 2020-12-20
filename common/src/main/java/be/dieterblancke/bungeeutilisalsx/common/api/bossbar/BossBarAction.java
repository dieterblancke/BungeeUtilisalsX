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

package be.dieterblancke.bungeeutilisalsx.common.api.bossbar;

import com.google.common.collect.Lists;

import java.util.List;

public class BossBarAction
{

    public static final BossBarAction ADD;
    public static final BossBarAction REMOVE;
    public static final BossBarAction UPDATE_HEALTH;
    public static final BossBarAction UPDATE_TITLE;
    public static final BossBarAction UPDATE_STYLE;
    public static final BossBarAction UPDATE_FLAGS;

    public static final List<BossBarAction> values;

    static
    {
        ADD = new BossBarAction( 0 );
        REMOVE = new BossBarAction( 1 );
        UPDATE_HEALTH = new BossBarAction( 2 );
        UPDATE_TITLE = new BossBarAction( 3 );
        UPDATE_STYLE = new BossBarAction( 4 );
        UPDATE_FLAGS = new BossBarAction( 5 );

        values = Lists.newArrayList( ADD, REMOVE, UPDATE_HEALTH, UPDATE_TITLE, UPDATE_STYLE, UPDATE_FLAGS );
    }

    private final int id;

    public BossBarAction( int id )
    {
        this.id = id;
    }

    public static BossBarAction[] values()
    {
        return values.toArray( new BossBarAction[values.size()] );
    }

    public static BossBarAction fromId( int action )
    {
        return values.stream().filter( a -> a.id == action ).findFirst().orElse( ADD );
    }

    public int getId()
    {
        return id;
    }

    @Override
    public boolean equals( Object obj )
    {
        return obj == this || ( obj instanceof BossBarAction && ( (BossBarAction) obj ).getId() == id );
    }
}