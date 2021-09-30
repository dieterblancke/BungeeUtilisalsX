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

package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.impl.InputPlaceHolderImpl;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript.Script;

import java.util.Optional;

public class JavaScriptPlaceHolder extends InputPlaceHolderImpl
{

    public JavaScriptPlaceHolder()
    {
        super( false, "javascript" );
    }

    @Override
    public String getReplacement( InputPlaceHolderEvent event )
    {
        final Optional<Script> optional = BuX.getInstance().getScripts()
                .stream()
                .filter( s -> s.getFile().equalsIgnoreCase( event.getArgument() ) )
                .findFirst();

        if ( optional.isPresent() )
        {
            return optional.get().getReplacement( event.getUser() );
        }
        else
        {
            return "script not found";
        }
    }
}