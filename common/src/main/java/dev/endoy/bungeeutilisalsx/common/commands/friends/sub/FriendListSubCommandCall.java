package dev.endoy.bungeeutilisalsx.common.commands.friends.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendData;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendUtils;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
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

        BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() ).thenAccept( pendingRequests ->
        {
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

            MessagePlaceholders messagePlaceholders = MessagePlaceholders.create()
                .append( "friendAmount", allFriends.size() )
                .append( "maxFriends", friendLimit )
                .append( "pendingFriends", pendingRequests )
                .append( "previousPage", previous )
                .append( "currentPage", page )
                .append( "nextPage", next )
                .append( "maxPages", pages );

            List<FriendData> friends = allFriends.subList( minNumber, maxNumber );
            user.sendLangMessage( "friends.list.head", messagePlaceholders );

            final String now = user.getLanguageConfig().getConfig().getString( "friends.list.online" );
            final String onlineText = user.getLanguageConfig().getConfig().getString( "friends.list.status.online" );
            final String offlineText = user.getLanguageConfig().getConfig().getString( "friends.list.status.offline" );

            for ( FriendData friend : friends )
            {
                user.sendLangMessage(
                    "friends.list.format",
                    MessagePlaceholders.create()
                        .append( "friendName", friend.getFriend() )
                        .append( "lastOnline", friend.isOnline() ? now : Utils.formatDate( friend.getLastOnline(), user.getLanguageConfig().getConfig() ) )
                        .append( "online", BuX.getApi().getPlayerUtils().isOnline( friend.getFriend() ) ? onlineText : offlineText )
                        .append( "friendSince", Utils.formatDate( friend.getFriendSince(), user.getLanguageConfig().getConfig() ) )
                );
            }
            user.sendLangMessage( "friends.list.foot", messagePlaceholders );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Lists your current friends.";
    }

    @Override
    public String getUsage()
    {
        return "/friend list [page]";
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
