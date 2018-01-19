package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

/*
 * Created by DBSoftwares on 19/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserBanEvent extends AbstractEvent implements Cancellable {

    private User banner;
    private UUID bannedUUID;
    private String bannedName;
    private String bannedIP;
    private String reason;
    private String executionServer;

    private boolean cancelled = false;

    public UserBanEvent(User banner, UUID bannedUUID, String bannedName, String bannedIP, String reason, String executionServer) {
        this.banner = banner;
        this.bannedUUID = bannedUUID;
        this.bannedName = bannedName;
        this.bannedIP = bannedIP;
        this.reason = reason;
        this.executionServer = executionServer;
    }

    public Optional<User> getBannedUser() {
        return getApi().getUser(bannedName);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
