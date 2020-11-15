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

package com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment;

import com.dbsoftwares.bungeeutilisalsx.common.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPunishRemoveEvent extends AbstractEvent implements Cancellable
{

    private PunishmentRemovalAction action;
    private User executor;
    private UUID uuid;
    private String name;
    private String ip;
    private String executionServer;
    private Date date = new Date( System.currentTimeMillis() );
    private boolean cancelled = false;

    public UserPunishRemoveEvent( PunishmentRemovalAction action, User executor, UUID uuid, String name, String ip, String executionServer )
    {
        this.action = action;
        this.executor = executor;
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.executionServer = executionServer;
    }

    public Optional<User> getUser()
    {
        return getApi().getUser( name );
    }

    public enum PunishmentRemovalAction
    {
        UNBAN, UNBANIP, UNMUTE, UNMUTEIP
    }
}