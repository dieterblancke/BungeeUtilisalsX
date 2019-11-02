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
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendUtils;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FriendRemoveRequestSubCommand extends SubCommand
{

    public FriendRemoveRequestSubCommand()
    {
        super(
                "removerequest", 1, 1,
                Arrays.asList( FileLocation.FRIENDS_CONFIG.getConfiguration().getString( "subcommands.removerequest.aliases" ).split( ", " ) )
        );
    }

    @Override
    public String getUsage()
    {
        return "/friends removerequest (name)";
    }

    @Override
    public String getPermission()
    {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString( "subcommands.removerequest.permission" );
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        final String name = args[0];
        final int friendLimit = FriendUtils.getFriendsLimit( user );
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final Optional<User> optionalTarget = BUCore.getApi().getUser( name );
        final UserStorage storage;

        if ( optionalTarget.isPresent() )
        {
            storage = optionalTarget.get().getStorage();
        } else
        {
            if ( !dao.getUserDao().exists( args[0] ) )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            storage = dao.getUserDao().getUserData( name );
        }

        if ( !dao.getFriendsDao().hasOutgoingFriendRequest( user.getUuid(), storage.getUuid() ) )
        {
            user.sendLangMessage( "friends.removerequest.no-request", "{user}", name );
            return;
        }

        dao.getFriendsDao().removeFriendRequest( storage.getUuid(), user.getUuid() );
        user.sendLangMessage( "friends.removerequest.removed", "{user}", name );

        optionalTarget.ifPresent( target -> target.sendLangMessage( "friends.removerequest.request-removed", "{user}", user.getName() ) );
    }

    @Override
    public List<String> getCompletions( User user, String[] args )
    {
        return null;
    }
}
