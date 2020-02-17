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
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageCommand extends BUCommand
{

    public LanguageCommand()
    {
        super(
                "language",
                Arrays.asList( FileLocation.GENERALCOMMANDS.getConfiguration().getString( "language.aliases" ).split( ", " ) ),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString( "language.permission" )
        );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return BUCore.getApi().getLanguageManager().getLanguages().stream().map( Language::getName ).collect( Collectors.toList() );
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        final String languages = BUCore.getApi().getLanguageManager().getLanguages().stream()
                .map( Language::getName )
                .collect( Collectors.joining( ", " ) );

        if ( args.length != 1 )
        {
            user.sendLangMessage( "general-commands.language.usage", "{languages}", languages );
            return;
        }
        final String langName = args[0];

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
