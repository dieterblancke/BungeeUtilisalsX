package be.dieterblancke.bungeeutilisalsx.webapi.query.friend;

import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Friend;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendResolver implements GraphQLResolver<Friend>
{

    private final UserService userService;

    @Cacheable
    public User getUser( final Friend friend )
    {
        return userService.findByUuid( friend.getUserId() );
    }

    @Cacheable
    public User getFriend( final Friend friend )
    {
        return userService.findByUuid( friend.getFriendId() );
    }
}