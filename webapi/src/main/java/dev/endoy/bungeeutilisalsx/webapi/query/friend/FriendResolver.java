package dev.endoy.bungeeutilisalsx.webapi.query.friend;

import dev.endoy.bungeeutilisalsx.webapi.dto.Friend;
import dev.endoy.bungeeutilisalsx.webapi.dto.User;
import dev.endoy.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendResolver implements GraphQLResolver<Friend>
{

    private final UserService userService;

    public User getUser( final Friend friend )
    {
        return userService.findByUuid( friend.getUserId() );
    }

    public User getFriend( final Friend friend )
    {
        return userService.findByUuid( friend.getFriendId() );
    }
}