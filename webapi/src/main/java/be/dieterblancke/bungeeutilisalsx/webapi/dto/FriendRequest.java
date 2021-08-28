package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class FriendRequest
{

    UUID userId;
    UUID friendId;
    LocalDateTime requestedAt;

    @Cacheable
    public static FriendRequest of( final be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest request )
    {
        return new FriendRequest(
                request.getUser(),
                request.getFriend(),
                new Timestamp( request.getRequestedAt().getTime() ).toLocalDateTime()
        );
    }
}
