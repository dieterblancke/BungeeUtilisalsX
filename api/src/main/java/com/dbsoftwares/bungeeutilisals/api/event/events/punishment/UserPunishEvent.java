package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

/*
 * Created by DBSoftwares on 19/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPunishEvent extends AbstractEvent implements Cancellable {

    private PunishmentType type;
    private User executor;
    private UUID UUID;
    private String name;
    private String ip;
    private String reason;
    private String executionServer;
    private Long expire;

    private boolean cancelled = false;

    public UserPunishEvent(PunishmentType type, User executor, UUID uuid, String name, String ip, String reason, String executionServer, Long expire) {
        this.type = type;
        this.executor = executor;
        this.UUID = uuid;
        this.name = name;
        this.ip = ip;
        this.reason = reason;
        this.executionServer = executionServer;
        this.expire = expire;
    }

    public boolean isActivatable() {
        return type.isActivatable();
    }

    public boolean isTemporary() {
        return type.isTemporary();
    }

    public Optional<User> getUser() {
        return getApi().getUser(name);
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
