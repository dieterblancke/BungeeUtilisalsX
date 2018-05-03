package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

/*
 * Created by DBSoftwares on 19/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPunishRemoveEvent extends AbstractEvent implements Cancellable {

    private PunishmentRemovalAction action;
    private User executor;
    private UUID UUID;
    private String name;
    private String ip;
    private String executionServer;
    private Date date = new Date(System.currentTimeMillis());
    private boolean cancelled = false;

    public UserPunishRemoveEvent(PunishmentRemovalAction action, User executor, UUID uuid, String name, String ip, String executionServer) {
        this.action = action;
        this.executor = executor;
        this.UUID = uuid;
        this.name = name;
        this.ip = ip;
        this.executionServer = executionServer;
    }

    public Optional<User> getUser() {
        return getApi().getUser(name);
    }

    public enum PunishmentRemovalAction {
        UNBAN, UNBANIP, UNMUTE, UNMUTEIP
    }
}