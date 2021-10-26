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
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.FriendBroadcastJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;
import java.util.stream.Collectors;

public class FriendBroadcastSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.broadcast.usage" );
            return;
        }
        if ( !user.getFriendSettings().getSetting( FriendSetting.FRIEND_BROADCAST, true ) )
        {
            user.sendLangMessage( "friends.broadcast.disabled" );
            return;
        }

        final String message = String.join( " ", args );
        BuX.getInstance().getJobManager().executeJob( new FriendBroadcastJob(
                user.getUuid(),
                user.getName(),
                message,
                user.getFriends()
                        .stream()
                        .map( FriendData::getFriend )
                        .collect( Collectors.toList() )
        ) );
    }

    @Override
    public String getDescription()
    {
        return "Broadcasts a message to all your online friends.";
    }

    @Override
    public String getUsage()
    {
        return "/friend broadcast (message)";
    }
}
