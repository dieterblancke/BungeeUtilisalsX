package com.dbsoftwares.bungeeutilisals.api.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendData {

    private UUID identifier;
    private String name;
    private Long friendSince;
    private boolean online;
    private Long lastOnline;

    public Date getLastOnlineDate() {
        return new Date(lastOnline);
    }
}