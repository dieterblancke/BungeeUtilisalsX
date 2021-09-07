package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Friend
{

    UUID userId;
    UUID friendId;
    LocalDateTime created;

    public static Friend of( final UUID uuid, final FriendData friendData )
    {
        return new Friend(
                uuid,
                friendData.getUuid(),
                new Timestamp( friendData.getFriendSince().getTime() ).toLocalDateTime()
        );
    }
}
