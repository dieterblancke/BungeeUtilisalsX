/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.dbsoftwares.bungeeutilisals.packet;

import com.dbsoftwares.bungeeutilisals.packet.packets.Packet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class PacketData {

    private Class<? extends Packet> packetClass;
    private Object direction;
    private int[] protocolIds;

    public PacketData(Class<? extends Packet> packetClass, Object direction, int... protocolIds) {
        this.packetClass = packetClass;
        this.direction = direction;
        this.protocolIds = protocolIds;
    }
}
