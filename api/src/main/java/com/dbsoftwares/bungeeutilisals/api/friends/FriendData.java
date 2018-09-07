package com.dbsoftwares.bungeeutilisals.api.friends;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendData {

    private UUID uuid;
    private String friend;
    private Date friendSince;
    private boolean online;
    private Date lastOnline;

    public FriendData(UUID uuid, String friend, Date friendSince, Date lastSeen) {
        this.uuid = uuid;
        this.friend = friend;
        this.friendSince = friendSince;
        this.online = BUCore.getApi().getPlayerUtils().isOnline(friend);
    }
}