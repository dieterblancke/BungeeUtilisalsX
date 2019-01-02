/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

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
    private UUID uuid;
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
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.reason = reason;
        this.executionServer = executionServer;
        this.expire = expire;
    }

    public PunishmentInfo getInfo() {
        return new PunishmentInfo(type, 0, name, ip, uuid, executor.getName(),
                executionServer, reason, date, expire, true, null);
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

    public boolean isIPPunishment() {
        return type.toString().startsWith("IP");
    }

    public boolean isUserPunishment() {
        return !type.toString().startsWith("IP");
    }
}