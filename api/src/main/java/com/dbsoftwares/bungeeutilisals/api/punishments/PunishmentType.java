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

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;

public enum PunishmentType {

    BAN(true, false), TEMPBAN(true, true), IPBAN(true, false), IPTEMPBAN(true, true),
    MUTE(true, false), TEMPMUTE(true, true), IPMUTE(true, false), IPTEMPMUTE(true, true),
    KICK(false, false), WARN(false, false);

    private final boolean activatable;
    private final boolean temporary;
    private final String tablePlaceHolder;

    PunishmentType(boolean activatable, boolean temporary) {
        this.activatable = activatable;
        this.temporary = temporary;

        final String toString = toString();
        final String type;

        if (toString.contains("BAN")) {
            type = "ban";
        } else if (toString.contains("MUTE")) {
            type = "mute";
        } else {
            type = toString.toLowerCase();
        }
        this.tablePlaceHolder = "{" + type + "s-table}";
    }

    public boolean isActivatable() {
        return activatable;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public String getTablePlaceHolder() {
        return tablePlaceHolder;
    }

    public String getTable() {
        return PlaceHolderAPI.formatMessage(getTablePlaceHolder());
    }

    public boolean isIP() {
        return toString().startsWith("IP");
    }

    public boolean isBan() {
        return toString().contains("BAN");
    }

    public boolean isMute() {
        return toString().contains("MUTE");
    }
}