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

package com.dbsoftwares.bungeeutilisals.api.utils.other;

import java.util.Iterator;
import java.util.List;

public class RandomIterator<T> implements Iterator<T>
{

    private final List<T> data;
    private final int[] iteratedIndexes;

    public RandomIterator( final List<T> data )
    {
        this.data = data;
        this.iteratedIndexes = new int[data.size()];
    }

    @Override
    public boolean hasNext()
    {
        return countZeroValues() > 0;
    }

    @Override
    public T next()
    {
        // TODO: get random
        return null;
    }

    private int countZeroValues()
    {
        int zeroValues = 0;

        for ( int i = 0; i < iteratedIndexes.length; i++ )
        {
            if ( iteratedIndexes[i] == 0 )
            {
                zeroValues++;
            }
        }

        return zeroValues;
    }
}
