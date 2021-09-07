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
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Lists;

import java.util.List;

public class FriendListSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final List<FriendData> allFriends = this.filterPlayerList( user.getFriends() );

        if ( allFriends.isEmpty() )
        {
            user.sendLangMessage( "friends.list.no-friends" );
            return;
        }

        final int friendLimit = FriendUtils.getFriendLimit( user );
        final int pendingRequests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() ).size();
        final int pages = (int) Math.ceil( (double) allFriends.size() / 15 );
        final int page;

        if ( args.size() >= 1 )
        {
            if ( MathUtils.isInteger( args.get( 0 ) ) )
            {
                final int tempPage = Integer.parseInt( args.get( 0 ) );

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

        if ( maxNumber > allFriends.size() )
        {
            maxNumber = allFriends.size();
        }

        final List<FriendData> friends = allFriends.subList( minNumber, maxNumber );
        user.sendLangMessage(
                "friends.list.head",
                "{friendAmount}", allFriends.size(),
                "{maxFriends}", friendLimit,
                "{pendingFriends}", pendingRequests,
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages
        );

        final String now = user.getLanguageConfig().getConfig().getString( "friends.list.online" );
        final String onlineText = user.getLanguageConfig().getConfig().getString( "friends.list.status.online" );
        final String offlineText = user.getLanguageConfig().getConfig().getString( "friends.list.status.offline" );

        for ( FriendData friend : friends )
        {
            user.sendLangMessage(
                    "friends.list.format",
                    "{friendName}", friend.getFriend(),
                    "{lastOnline}", friend.isOnline() ? now : Utils.formatDate( friend.getLastOnline(), user.getLanguageConfig().getConfig() ),
                    "{online}", BuX.getApi().getPlayerUtils().isOnline( friend.getFriend() ) ? onlineText : offlineText,
                    "{friendSince}", Utils.formatDate( friend.getFriendSince(), user.getLanguageConfig().getConfig() )
            );
        }
        user.sendLangMessage(
                "friends.list.foot",
                "{friendAmount}", allFriends.size(),
                "{maxFriends}", friendLimit,
                "{pendingFriends}", pendingRequests,
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages
        );
    }

    private List<FriendData> filterPlayerList( final List<FriendData> orig )
    {
        final List<FriendData> result = Lists.newArrayList();

        for ( FriendData data : orig )
        {
            if ( StaffUtils.isHidden( data.getFriend() ) )
            {
                continue;
            }
            result.add( data );
        }
        return result;
    }
}
