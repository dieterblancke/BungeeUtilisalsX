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

package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.EqualsAndHashCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode( callSuper = true )
public class InputPlaceHolder extends PlaceHolder
{

    private final Pattern pattern;

    public InputPlaceHolder( boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler )
    {
        super( null, requiresUser, handler );

        this.pattern = makePlaceholderWithArgsPattern( prefix );
    }

    private static Pattern makePlaceholderWithArgsPattern( String prefix )
    {
        return Pattern.compile( "(\\{" + Pattern.quote( prefix ) + ":)(.+?)(})" );
    }

    private static String extractArgumentFromPlaceholder( Matcher matcher )
    {
        return matcher.group( 2 ).trim();
    }

    @Override
    public String format( User user, String message )
    {
        Matcher matcher = pattern.matcher( message );

        while ( matcher.find() )
        {
            final String argument = extractArgumentFromPlaceholder( matcher );
            final InputPlaceHolderEvent event = new InputPlaceHolderEvent( user, this, message, argument );

            message = message.replace( matcher.group(), eventHandler.getReplacement( event ) );
        }

        return message;
    }
}