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
import com.dbsoftwares.bungeeutilisals.packet.utils.PacketUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

public class PacketRegistry
{

    private static List<ProtocolConstants> versions = Lists.newArrayList();
    private static List<PacketData> registry = Lists.newArrayList();

    static
    {
        // when version not supported, -1 will be provided | ids are in order of Version (enum) versions
        /*
         * Version URLs:
         * - 1.14: https://wiki.vg/index.php?title=Protocol&oldid=14963
         * - 1.13.2: https://wiki.vg/index.php?title=Protocol&oldid=14889
         * - 1.13.1: https://wiki.vg/index.php?title=Protocol&oldid=14301
         * - 1.12.2: https://wiki.vg/index.php?title=Protocol&oldid=14204
         */


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
//        registry.add( new PacketData(
//                PacketPlayOutBossBar.class,
//                getClientDirection( Protocol.GAME ),
//                ProtocolMapping.map( Version.MINECRAFT_1_9, 0x0C )
//        ) );
    }

    public static Object getServerDirection( final Protocol protocol )
    {
        try
        {
            final Field field = protocol.getClass().getSuperclass().getDeclaredField( "TO_SERVER" );

            field.setAccessible( true );

            return field.get( protocol );
        }
        catch ( NoSuchFieldException | IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static Object getClientDirection( final Protocol protocol )
    {
        try
        {
            final Field field = protocol.getClass().getSuperclass().getDeclaredField( "TO_CLIENT" );

            field.setAccessible( true );

            return field.get( protocol );
        }
        catch ( NoSuchFieldException | IllegalAccessException e )
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

            Array.set( mappings, i, PacketUtils.createProtocolMapping( mapping.getVersion().getVersionId(), mapping.getPacket() ) );
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
