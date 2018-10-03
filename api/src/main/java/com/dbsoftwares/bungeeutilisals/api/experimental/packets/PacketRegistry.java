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

package com.dbsoftwares.bungeeutilisals.api.experimental.packets;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInPlayerPosition;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayOutBossBar;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.google.common.collect.Lists;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.List;

public class PacketRegistry {

    private static List<ProtocolConstants> versions = Lists.newArrayList();
    private static List<PacketData> registry = Lists.newArrayList();

    static {
        // when version not supported, -1 will be provided | ids are in order of Version (enum) versions
        registry.add(new PacketData(PacketPlayOutBossBar.class, Protocol.GAME.TO_CLIENT, -1, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C));
        registry.add(new PacketData(PacketPlayInPlayerPosition.class, Protocol.GAME.TO_SERVER, 0x04, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0E, 0x0D, 0x0D, 0x10, 0x10));
    }

    public static void registerPackets() {
        for (final PacketData data : registry) {
            List<Object> mappings = Lists.newArrayList();

            for (int i = 0; i < data.getProtocolIds().length; i++) {
                int id = data.getProtocolIds()[i];

                System.out.println(Version.values()[i].getVersion() + "   " + id);

                if (id == -1) {
                    continue;
                }
                mappings.add(Utils.createProtocolMapping(Version.values()[i].getVersion(), id));
            }

            Utils.registerPacket(data.getDirection(), data.getPacketClass(), mappings.toArray(new Object[0]));
        }
    }
}
