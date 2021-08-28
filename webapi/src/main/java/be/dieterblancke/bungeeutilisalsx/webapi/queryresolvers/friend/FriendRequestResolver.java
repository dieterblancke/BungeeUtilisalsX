package be.dieterblancke.bungeeutilisalsx.webapi.queryresolvers.friend;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Friend;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestResolver implements GraphQLResolver<FriendRequest>
{

    @Cacheable
    public User getUser( final FriendRequest friend )
    {
        return User.of(
                BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( friend.getUserId() )
        );
    }

    @Cacheable
    public User getFriend( final FriendRequest friend )
    {
        return User.of(
                BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( friend.getFriendId() )
        );
    }
}