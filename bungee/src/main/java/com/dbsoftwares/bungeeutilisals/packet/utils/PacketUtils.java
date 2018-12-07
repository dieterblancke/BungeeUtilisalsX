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

package com.dbsoftwares.bungeeutilisals.packet.utils;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class PacketUtils {

    public static boolean registerPacket(Protocol.DirectionData direction, Class<? extends DefinedPacket> packetClass, Object... protocolMappings) {
        try {
            Class<?> protocolMap = Class.forName("net.md_5.bungee.protocol.Protocol$ProtocolMapping");

            Object map = Array.newInstance(protocolMap, protocolMappings.length);

            for (int i = 0; i < protocolMappings.length; i++) {
                Array.set(map, i, protocolMappings[i]);
            }

            Method register = Protocol.DirectionData.class.getDeclaredMethod("registerPacket", Class.class, map.getClass());
            register.setAccessible(true);

            register.invoke(direction, packetClass, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Object createProtocolMapping(int protocol, int id) {
        try {
            Method map = Protocol.class.getDeclaredMethod("map", int.class, int.class);
            map.setAccessible(true);

            return map.invoke(null, protocol, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getProxyProtocol() {
        return ProtocolConstants.SUPPORTED_VERSION_IDS.get(ProtocolConstants.SUPPORTED_VERSION_IDS.size() - 1);
    }
}