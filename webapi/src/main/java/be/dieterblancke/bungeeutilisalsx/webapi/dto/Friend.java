package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Friend
{

    UUID friendId;
    LocalDateTime created;

    public static Friend of( final FriendData friendData )
    {
        return new Friend(
                friendData.getUuid(),
                new Timestamp( friendData.getFriendSince().getTime() ).toLocalDateTime()
        );
    }
}
