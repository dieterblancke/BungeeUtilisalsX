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

package be.dieterblancke.bungeeutilisalsx.common.commands.general.message;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.UserDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class IgnoreCommandCall implements CommandCall, TabCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.ignore.usage" );
            return;
        }
        final String action = args.get( 0 );

        if ( action.equalsIgnoreCase( "add" ) || action.equalsIgnoreCase( "remove" ) )
        {
            if ( args.size() != 2 )
            {
                user.sendLangMessage( "general-commands.ignore.usage" );
                return;
            }
            final String name = args.get( 1 );

            if ( user.getName().equalsIgnoreCase( name ) )
            {
                user.sendLangMessage( "general-commands.ignore.self-ignore" );
                return;
            }

            final UserDao dao = BuX.getApi().getStorageManager().getDao().getUserDao();

            if ( !dao.exists( name ) )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            final UserStorage storage = dao.getUserData( name );

            if ( action.equalsIgnoreCase( "remove" ) )
            {
                if ( user.getStorage().getIgnoredUsers().stream().noneMatch( ignored -> ignored.equalsIgnoreCase( name ) ) )
                {
                    user.sendLangMessage( "general-commands.ignore.remove.not-ignored", "{user}", name );
                    return;
                }

                BuX.getApi().getStorageManager().getDao().getUserDao().unignoreUser( user.getUuid(), storage.getUuid() );
                user.getStorage().getIgnoredUsers().removeIf( ignored -> ignored.equalsIgnoreCase( name ) );

                user.sendLangMessage( "general-commands.ignore.remove.unignored", "{user}", name );
            }
            else
            {
                if ( user.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( name ) ) )
                {
                    user.sendLangMessage( "general-commands.ignore.add.already-ignored", "{user}", name );
                    return;
                }

                BuX.getApi().getStorageManager().getDao().getUserDao().ignoreUser( user.getUuid(), storage.getUuid() );
                user.getStorage().getIgnoredUsers().add( storage.getUserName() );

                user.sendLangMessage( "general-commands.ignore.add.ignored", "{user}", name );
            }
        }
        else if ( action.equalsIgnoreCase( "list" ) )
        {
            if ( user.getStorage().getIgnoredUsers().isEmpty() )
            {
                user.sendLangMessage( "general-commands.ignore.list.none" );
            }
            else
            {
                user.sendLangMessage(
                        "general-commands.ignore.list.message",
                        "{ignoredusers}",
                        String.join(
                                user.getLanguageConfig().getConfig().getString( "general-commands.ignore.list.separator" ),
                                user.getStorage().getIgnoredUsers()
                        )
                );
            }
        }
        else
        {
            user.sendLangMessage( "general-commands.ignore.usage" );
        }
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }
}
