package dev.endoy.bungeeutilisalsx.common.api.friends;

import dev.endoy.bungeeutilisalsx.common.BuX;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class FriendData
{

    private UUID uuid;
    private String friend;
    private Date friendSince;
    private Date lastOnline;

    public FriendData()
    {
    }

    public FriendData( final UUID uuid, final String friend, final Date friendSince, final Date lastSeen )
    {
        this.uuid = uuid;
        this.friend = friend;
        this.friendSince = friendSince;
        this.lastOnline = lastSeen;
    }

    public boolean isOnline()
    {
        return BuX.getApi().getPlayerUtils().isOnline( friend );
    }
}