package be.dieterblancke.bungeeutilisalsx.webapi.queryresolvers.friend;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Friend;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class FriendResolver implements GraphQLResolver<Friend>
{

    public User getFriend( final Friend friend )
    {
        return User.of(
                BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( friend.getFriendId() )
        );
    }
}