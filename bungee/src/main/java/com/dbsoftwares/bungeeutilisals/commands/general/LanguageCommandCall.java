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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return BUCore.getApi().getLanguageManager().getLanguages().stream().map( Language::getName ).collect( Collectors.toList() );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final String languages = BUCore.getApi().getLanguageManager().getLanguages().stream()
                .map( Language::getName )
                .collect( Collectors.joining( ", " ) );

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.language.usage", "{languages}", languages );
            return;
        }
        final String langName = args.get( 0 );

        if ( user.getLanguage().getName().equalsIgnoreCase( langName ) )
        {
            user.sendLangMessage( "general-commands.language.already", "{language}", langName );
            return;
        }

        final Optional<Language> optional = BUCore.getApi().getLanguageManager().getLanguage( langName );

        if ( optional.isPresent() )
        {
            final Language language = optional.get();

            user.setLanguage( language );
            BUCore.getApi().getStorageManager().getDao().getUserDao().setLanguage( user.getUuid(), language );

            user.sendLangMessage( "general-commands.language.changed", "{language}", language.getName() );
        }
        else
        {
            user.sendLangMessage(
                    "general-commands.language.notfound",
                    "{language}", langName, "{languages}", languages
            );
        }
    }
}
