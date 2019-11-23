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

package com.dbsoftwares.bungeeutilisals.commands.friends.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettingType;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettings;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendSettingsSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length == 0 )
        {
            final FriendSettings settings = user.getFriendSettings();
            user.sendLangMessage( "friends.settings.noargs.header" );

            for ( FriendSettingType setting : FriendSettingType.values() )
            {
                user.sendLangMessage(
                        "friends.settings.noargs.format",
                        "{type}", setting.getName( user.getLanguageConfig() ),
                        "{status}", user.getLanguageConfig().getString( "friends.settings.noargs." + (settings.check( setting ) ? "enabled" : "disabled") )
                );
            }
        }
        else if ( args.length == 2 )
        {
            final FriendSettingType type = Utils.valueOfOr( FriendSettingType.class, args[0].toUpperCase(), null );
            final boolean value = !args[1].toLowerCase().contains( "d" );

            if ( type == null )
            {
                final String settings = Stream.of( FriendSettingType.values() )
                        .map( t -> t.getName( user.getLanguageConfig() ) )
                        .collect( Collectors.joining() );

                user.sendLangMessage( "friends.settings.invalid", "{settings}", settings );
                return;
            }

            user.getFriendSettings().set( type, value );
            BUCore.getApi().getStorageManager().getDao().getFriendsDao().setSetting( user.getUuid(), type, value );

            user.sendLangMessage( "friends.settings.updated", "{type}", type.toString().toLowerCase() );
        }
        else
        {
            user.sendLangMessage( "friends.settings.usage" );
        }
    }
}
