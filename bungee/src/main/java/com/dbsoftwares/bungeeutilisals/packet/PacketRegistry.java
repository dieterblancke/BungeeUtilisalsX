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

import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.packet.packets.position.PacketPlayInPlayerPosition;
import com.dbsoftwares.bungeeutilisals.packet.utils.PacketUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.List;

public class PacketRegistry {

    private static List<ProtocolConstants> versions = Lists.newArrayList();
    private static List<PacketData> registry = Lists.newArrayList();

    static {
        // when version not supported, -1 will be provided | ids are in order of Version (enum) versions

        // https://wiki.vg/Protocol#Player_Position
        registry.add(new PacketData(PacketPlayInPlayerPosition.class, Protocol.GAME.TO_SERVER, 0x04, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0E, 0x0D, 0x0D, 0x10, 0x10, 0x10));

        /* For if we ever (try to) add back the inventory packets
        registry.add(new PacketData(PacketPlayInCloseWindow.class, Protocol.GAME.TO_SERVER, 0x0D, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x09, 0x08, 0x08, 0x09, 0x09, 0x09));
        registry.add(new PacketData(PacketPlayInWindowClick.class, Protocol.GAME.TO_SERVER, 0x0E, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x08, 0x07, 0x07, 0x08, 0x08, 0x08));
        registry.add(new PacketData(PacketPlayOutOpenWindow.class, Protocol.GAME.TO_CLIENT, 0x2D, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x14, 0x14, 0x14));
        registry.add(new PacketData(PacketPlayOutCloseWindow.class, Protocol.GAME.TO_CLIENT, 0x2E, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x13, 0x13, 0x13));
        registry.add(new PacketData(PacketPlayOutWindowItems.class, Protocol.GAME.TO_CLIENT, 0x30, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x15, 0x15, 0x15));
        */
    }

    public static void registerPacket(final PacketData data) {
        if (!registry.contains(data)) {
            registry.add(data);
        }
        List<Object> mappings = Lists.newArrayList();

        for (int i = 0; i < data.getProtocolIds().length; i++) {
            final int id = data.getProtocolIds()[i];

            if (id == -1) {
                continue;
            }
            final int versionId = Version.values()[i].getVersion();

            if (PacketUtils.getProxyProtocol() < versionId) {
                continue; // avoiding errors for older bungee versions
            }
            mappings.add(PacketUtils.createProtocolMapping(versionId, id));
        }

        PacketUtils.registerPacket(data.getDirection(), data.getPacketClass(), mappings.toArray(new Object[0]));
    }

    public static void registerPackets() {
        for (final PacketData data : registry) {
            registerPacket(data);
        }
    }
}
