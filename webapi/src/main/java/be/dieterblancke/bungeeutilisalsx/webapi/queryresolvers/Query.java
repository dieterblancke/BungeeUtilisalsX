package be.dieterblancke.bungeeutilisalsx.webapi.queryresolvers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Friend;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.FriendRequestType;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class Query implements GraphQLQueryResolver
{

    @Cacheable
    public User findUserByName( final String name )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( name );

        return storage.isLoaded() ? User.of( storage ) : null;
    }

    @Cacheable
    public User findUserByUuid( final UUID uuid )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( uuid );

        return storage.isLoaded() ? User.of( storage ) : null;
    }

    @Cacheable
    public List<Friend> findFriends( final UUID uuid )
    {
        final List<FriendData> friend = BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( uuid );

        return friend
                .stream()
                .map( data -> Friend.of( uuid, data ) )
                .collect( Collectors.toList() );
    }

    @Cacheable
    public List<FriendRequest> findFriendRequests( final UUID uuid, final FriendRequestType requestType )
    {
        final List<be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest> requests;

        if ( requestType == FriendRequestType.INCOMING )
        {
            requests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( uuid );
        }
        else
        {
            requests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( uuid );
        }

        return requests
                .stream()
                .map( FriendRequest::of )
                .collect( Collectors.toList() );
    }
}
