package dev.endoy.bungeeutilisalsx.common.commands.friends.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequest;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        final CompletableFuture<List<FriendRequest>> task;
        final String requestType;

        if ( type.contains( "out" ) )
        {
            task = BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( user.getUuid() );
            requestType = "outgoing";
        }
        else if ( type.contains( "in" ) )
        {
            task = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() );
            requestType = "incoming";
        }
        else
        {
            user.sendLangMessage( "friends.requests.usage" );
            return;
        }

        task.thenAccept( allRequests ->
        {
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

            MessagePlaceholders messagePlaceholders = MessagePlaceholders.create()
                .append( "previousPage", previous )
                .append( "currentPage", page )
                .append( "nextPage", next )
                .append( "maxPages", pages )
                .append( "requestAmount", allRequests.size() )
                .append( "type", user.getLanguageConfig().getConfig().getString( "friends.requests." + requestType ) )
                .append( "type_lowercase", requestType.toLowerCase() );

            List<FriendRequest> requests = allRequests.subList( minNumber, maxNumber );
            user.sendLangMessage( "friends.requests.head", messagePlaceholders );

            requests.forEach( request ->
            {
                final String targetName = requestType.equalsIgnoreCase( "outgoing" )
                    ? request.getFriendName() : request.getUserName();

                user.sendLangMessage(
                    "friends.requests.format." + requestType,
                    MessagePlaceholders.create()
                        .append( "target", targetName )
                        .append( "requestDate", Utils.formatDate( request.getRequestedAt(), user.getLanguageConfig().getConfig() ) )
                );
            } );
            user.sendLangMessage( "friends.requests.foot", messagePlaceholders );
        } );
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
