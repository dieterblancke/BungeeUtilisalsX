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
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;

public class FriendRequestsSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.requests.usage" );
            return;
        }
        final String type = args.get( 0 );
        final List<FriendRequest> allRequests;
        final String requestType;

        if ( type.contains( "out" ) )
        {
            allRequests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( user.getUuid() );
            requestType = "outgoing";
        }
        else if ( type.contains( "in" ) )
        {
            allRequests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() );
            requestType = "incoming";
        }
        else
        {
            user.sendLangMessage( "friends.requests.usage" );
            return;
        }

        if ( allRequests.isEmpty() )
        {
            user.sendLangMessage( "friends.requests.no-requests" );
            return;
        }

        final int pages = (int) Math.ceil( (double) allRequests.size() / 15 );
        final int page;

        if ( args.size() > 1 )
        {
            if ( MathUtils.isInteger( args.get( 1 ) ) )
            {
                final int tempPage = Integer.parseInt( args.get( 1 ) );

                page = Math.min( tempPage, pages );
            }
            else
            {
                page = 1;
            }
        }
        else
        {
            page = 1;
        }

        final int previous = page > 1 ? page - 1 : 1;
        final int next = Math.min( page + 1, pages );

        int maxNumber = page * 10;
        int minNumber = maxNumber - 10;

        if ( maxNumber > allRequests.size() )
        {
            maxNumber = allRequests.size();
        }

        final List<FriendRequest> requests = allRequests.subList( minNumber, maxNumber );
        user.sendLangMessage(
                "friends.requests.head",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages,
                "{requestAmount}", allRequests.size(),
                "{type}", user.getLanguageConfig().getConfig().getString( "friends.requests." + requestType ),
                "{type_lowercase}", requestType.toLowerCase()
        );

        requests.forEach( request ->
        {
            final String targetName = requestType.equalsIgnoreCase( "outgoing" )
                    ? request.getFriendName() : request.getUserName();

            user.sendLangMessage(
                    "friends.requests.format." + requestType,
                    "{target}", targetName,
                    "{requestDate}", Utils.formatDate( request.getRequestedAt(), user.getLanguageConfig().getConfig() )
            );
        } );
        user.sendLangMessage(
                "friends.requests.foot",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages,
                "{requestAmount}", allRequests.size(),
                "{type}", requestType,
                "{type_lowercase}", requestType.toLowerCase()
        );
    }

    @Override
    public String getDescription()
    {
        return "Lists all friend requests for a certain type (incoming / outgoing)";
    }

    @Override
    public String getUsage()
    {
        return "/friend requests (in / out) [page]";
    }
}
