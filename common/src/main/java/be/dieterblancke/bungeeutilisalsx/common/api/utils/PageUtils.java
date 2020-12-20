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

package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import lombok.Getter;

import java.util.List;

public class PageUtils
{

    public static <T> List<T> getPageFromList( final int page, final List<T> list, final int pageSize ) throws PageNotFoundException
    {
        final int maxPages = (int) Math.ceil( list.size() / (double) pageSize );

        if ( page > maxPages )
        {
            throw new PageNotFoundException( page, maxPages );
        }

        final int begin = ( ( page - 1 ) * 10 );
        int end = begin + 10;

        if ( end > list.size() )
        {
            end = list.size();
        }

        return list.subList( begin, end );
    }

    public static class PageNotFoundException extends Exception
    {

        @Getter
        private int page;
        @Getter
        private int maxPages;

        public PageNotFoundException( int page, int maxPages )
        {
            this.page = page;
            this.maxPages = maxPages;
        }
    }
}
