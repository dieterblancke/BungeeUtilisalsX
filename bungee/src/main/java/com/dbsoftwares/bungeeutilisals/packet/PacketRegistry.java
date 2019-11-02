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

package com.dbsoftwares.bungeeutilisals.packet;

import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.packet.packets.in.PacketPlayInPlayerPosition;
import com.dbsoftwares.bungeeutilisals.packet.packets.out.PacketPlayOutBossBar;
import com.dbsoftwares.bungeeutilisals.packet.utils.PacketUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.lang.reflect.Field;
import java.util.List;

public class PacketRegistry
{

    private static List<ProtocolConstants> versions = Lists.newArrayList();
    private static List<PacketData> registry = Lists.newArrayList();

    static
    {
        // when version not supported, -1 will be provided | ids are in order of Version (enum) versions

        // https://wiki.vg/Protocol#Player_Position
        registry.add( new PacketData(
                PacketPlayInPlayerPosition.class,
                getServerDirection( Protocol.GAME ),
                ProtocolMapping.map( Version.MINECRAFT_1_8, 0x04 ),
                ProtocolMapping.map( Version.MINECRAFT_1_9, 0x0C ),
                ProtocolMapping.map( Version.MINECRAFT_1_12, 0x0E ),
                ProtocolMapping.map( Version.MINECRAFT_1_12_1, 0x0D ),
                ProtocolMapping.map( Version.MINECRAFT_1_13, 0x10 )
        ) );
        // https://wiki.vg/Protocol#Boss_Bar
        registry.add( new PacketData(
                PacketPlayOutBossBar.class,
                getClientDirection( Protocol.GAME ),
                ProtocolMapping.map( Version.MINECRAFT_1_9, 0x0C )
        ) );

        /* For if we ever (try to) add back the inventory packets
        registry.add(new PacketData(PacketPlayInCloseWindow.class, Protocol.GAME.TO_SERVER, 0x0D, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x08, 0x09, 0x08, 0x08, 0x09, 0x09, 0x09));
        registry.add(new PacketData(PacketPlayInWindowClick.class, Protocol.GAME.TO_SERVER, 0x0E, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x07, 0x08, 0x07, 0x07, 0x08, 0x08, 0x08));
        registry.add(new PacketData(PacketPlayOutOpenWindow.class, Protocol.GAME.TO_CLIENT, 0x2D, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x13, 0x14, 0x14, 0x14));
        registry.add(new PacketData(PacketPlayOutCloseWindow.class, Protocol.GAME.TO_CLIENT, 0x2E, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x12, 0x13, 0x13, 0x13));
        registry.add(new PacketData(PacketPlayOutWindowItems.class, Protocol.GAME.TO_CLIENT, 0x30, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x14, 0x15, 0x15, 0x15));
        */
    }

    private static Object getServerDirection( final Protocol protocol )
    {
        try
        {
            final Field field = protocol.getClass().getSuperclass().getDeclaredField( "TO_SERVER" );

            field.setAccessible( true );

            return field.get( protocol );
        } catch ( NoSuchFieldException | IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static Object getClientDirection( final Protocol protocol )
    {
        try
        {
            final Field field = protocol.getClass().getSuperclass().getDeclaredField( "TO_CLIENT" );

            field.setAccessible( true );

            return field.get( protocol );
        } catch ( NoSuchFieldException | IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static void registerPacket( final PacketData data )
    {
        if ( !registry.contains( data ) )
        {
            registry.add( data );
        }
        final Object[] mappings = new Object[data.getMappings().length];

        for ( int i = 0; i < data.getMappings().length; i++ )
        {
            final ProtocolMapping mapping = data.getMappings()[i];

            mappings[i] =PacketUtils.createProtocolMapping( mapping.getVersion().getVersionId(), mapping.getPacket() );
        }

        PacketUtils.registerPacket( data.getDirection(), data.getPacketClass(), mappings );
    }

    public static void registerPackets()
    {
        for ( final PacketData data : registry )
        {
            registerPacket( data );
        }
    }
}
