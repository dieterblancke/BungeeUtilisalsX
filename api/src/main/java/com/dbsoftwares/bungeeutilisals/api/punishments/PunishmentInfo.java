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

package com.dbsoftwares.bungeeutilisals.api.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentInfo
{

    private PunishmentType type;

    private String id;
    private String user;
    private String ip;
    private UUID uuid;
    private String executedBy;
    private String server;
    private String reason;
    private Date date;

    // Not applicable for all punishments
    private Long expireTime;
    private boolean active;
    private String removedBy;

    public boolean isActivatable()
    {
        return type == null || type.isActivatable();
    }

    public boolean isTemporary()
    {
        return type != null && type.isTemporary();
    }
}