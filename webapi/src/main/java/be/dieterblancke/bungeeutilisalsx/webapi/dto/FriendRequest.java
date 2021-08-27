package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class FriendRequest
{

    UUID friendId;
    LocalDateTime requestedAt;

    public static FriendRequest of( final be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest request )
    {
        return new FriendRequest(
                request.getFriend(),
                new Timestamp( request.getRequestedAt().getTime() ).toLocalDateTime()
        );
    }
}
