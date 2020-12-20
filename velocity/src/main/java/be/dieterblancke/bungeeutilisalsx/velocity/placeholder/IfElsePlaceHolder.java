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

package be.dieterblancke.bungeeutilisalsx.velocity.placeholder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfElsePlaceHolder
{

    private static final Pattern pattern = Pattern.compile( "(\\{if \\((.+?)\\):)(.+?)(else:)(.+?)(})" );

    public static String format( String message, IfElseFormatter formatter )
    {
        Matcher matcher = pattern.matcher( message );

        while ( matcher.find() )
        {
            final String condition = matcher.group( 2 ).trim();
            final String ifResult = matcher.group( 3 ).trim();
            final String elseResult = matcher.group( 5 ).trim();

            final String result = formatter.format( condition, ifResult, elseResult );

            if ( result != null )
            {
                message = message.replace( matcher.group(), result );
            }
        }

        return message;
    }

    public interface IfElseFormatter
    {
        String format( String condition, String ifResult, String elseResult );
    }
}
