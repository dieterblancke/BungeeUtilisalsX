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

package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FriendSettingsSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            final FriendSettings settings = user.getFriendSettings();
            user.sendLangMessage( "friends.settings.noargs.header" );

            for ( FriendSetting setting : FriendSetting.getEnabledSettings() )
            {
                user.sendLangMessage(
                        "friends.settings.noargs.format",
                        "{type}", setting.getName( user.getLanguageConfig().getConfig() ),
                        "{status}", user.getLanguageConfig().getConfig().getString( "friends.settings.noargs." + ( settings.getSetting( setting, true ) ? "enabled" : "disabled" ) )
                );
            }

            user.sendLangMessage( "friends.settings.noargs.footer" );
        }
        else if ( args.size() == 2 )
        {
            final FriendSetting type = Utils.valueOfOr( FriendSetting.class, args.get( 0 ).toUpperCase(), null );

            if ( type == null )
            {
                final String settings = Stream.of( FriendSetting.values() )
                        .map( t -> t.getName( user.getLanguageConfig().getConfig() ) )
                        .collect( Collectors.joining() );

                user.sendLangMessage( "friends.settings.invalid", "{settings}", settings );
                return;
            }
            final boolean value = args.get( 1 ).contains( "toggle" )
                    ? !user.getFriendSettings().getSetting( type, false )
                    : !args.get( 1 ).toLowerCase().contains( "d" );

            user.getFriendSettings().set( type, value );
            BuX.getApi().getStorageManager().getDao().getFriendsDao().setSetting( user.getUuid(), type, value );

            user.sendLangMessage(
                    "friends.settings.updated",
                    "{type}", type.toString().toLowerCase(),
                    "{value}", value
            );
        }
        else
        {
            user.sendLangMessage( "friends.settings.usage" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Updates a setting value for one of the existing setting types.";
    }

    @Override
    public String getUsage()
    {
        return "/friend settings [setting] [value]";
    }
}
