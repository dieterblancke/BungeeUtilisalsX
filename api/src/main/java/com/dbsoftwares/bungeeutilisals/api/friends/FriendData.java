package com.dbsoftwares.bungeeutilisals.api.friends;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
public class FriendData {

    private UUID identifier;
    private String name;
    private Long friendSince;
    private Boolean isOnline;
    private Long lastOnline;

    /**
     * @return Returns the User Identifier.
     */
    public UUID getIdentifier() {
        return identifier;
    }

    /**
     * @return Returns the User Name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the time (in Millis) since when he is in the friend list.
     */
    public Long getFriendSince() {
        return friendSince;
    }

    /**
     * @return Returns the time (in Date object) since when he is in the friend list.
     */
    public Date getFriendSinceDate() {
        return new Date(friendSince);
    }

    /**
     * @return Returns true if the user is online, false if not.
     */
    public Boolean isOnline() {
        return isOnline;
    }

    /**
     * @return Returns the time (in Millis) since when the friend was last seen. <p>Returns current time if online.</p>
     */
    public Long getLastOnline() {
        return lastOnline;
    }

    /**
     * @return Returns the time (in Date object) since when the friend was last seen. <p>Returns current time if online.</p>
     */
    public Date getLastOnlineDate() {
        return new Date(lastOnline);
    }
}