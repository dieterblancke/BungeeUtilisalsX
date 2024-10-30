package dev.endoy.bungeeutilisalsx.common.api.friends;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FriendRequest
{

    private UUID user;
    private String userName;
    private UUID friend;
    private String friendName;
    private Date requestedAt;

}