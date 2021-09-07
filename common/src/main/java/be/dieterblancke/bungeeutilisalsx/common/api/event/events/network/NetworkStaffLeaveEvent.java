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

package be.dieterblancke.bungeeutilisalsx.common.api.event.events.network;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This event will be executed upon network join
 */
@Getter
@Setter
@EqualsAndHashCode( callSuper = true )
public class NetworkStaffLeaveEvent extends AbstractEvent implements Cancellable
{

    private final String userName;
    private final UUID uuid;
    private final String staffRank;
    private boolean cancelled;

    public NetworkStaffLeaveEvent( final String userName, final UUID uuid, final String staffRank )
    {
        this.userName = userName;
        this.uuid = uuid;
        this.staffRank = staffRank;
    }
}
