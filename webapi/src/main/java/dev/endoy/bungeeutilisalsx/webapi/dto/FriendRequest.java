package dev.endoy.bungeeutilisalsx.webapi.dto;

import dev.endoy.bungeeutilisalsx.webapi.caching.Cacheable;
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
    public static FriendRequest of( final dev.endoy.bungeeutilisalsx.common.api.friends.FriendRequest request )
    {
        return new FriendRequest(
            request.getUser(),
            request.getFriend(),
            new Timestamp( request.getRequestedAt().getTime() ).toLocalDateTime()
        );
    }
}
