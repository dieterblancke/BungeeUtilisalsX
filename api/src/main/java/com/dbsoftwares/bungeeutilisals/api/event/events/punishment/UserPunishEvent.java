package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

/*
 * Created by DBSoftwares on 19/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
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
    private Date date = new Date(System.currentTimeMillis());

    @Getter
    @Setter
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

    public PunishmentInfo getInfo() {
        return PunishmentInfo.builder().uuid(UUID).user(name).IP(ip).reason(reason)
                .server(executionServer).date(date).active(true).executedBy(executor.getName())
                .expireTime(expire).removedBy(null).type(type).build();
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

    public boolean isMute() {
        return type.toString().contains("MUTE");
    }

    public boolean isBan() {
        return type.toString().contains("BAN");
    }

    public boolean isKick() {
        return type.equals(PunishmentType.KICK);
    }

    public boolean isWarn() {
        return type.equals(PunishmentType.WARN);
    }
}