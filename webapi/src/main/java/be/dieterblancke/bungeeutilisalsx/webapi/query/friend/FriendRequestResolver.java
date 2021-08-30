package be.dieterblancke.bungeeutilisalsx.webapi.query.friend;

import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendRequestResolver implements GraphQLResolver<FriendRequest>
{

    private final UserService userService;

    @Cacheable
    public User getUser( final FriendRequest friend )
    {
        return userService.findByUuid( friend.getUserId() );
    }

    @Cacheable
    public User getFriend( final FriendRequest friend )
    {
        return userService.findByUuid( friend.getFriendId() );
    }
}