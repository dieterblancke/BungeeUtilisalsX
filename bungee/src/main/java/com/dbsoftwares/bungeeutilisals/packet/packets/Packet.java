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

package com.dbsoftwares.bungeeutilisals.packet.packets;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Optional;

public abstract class Packet extends DefinedPacket {

    @Override
    public abstract void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    @Override
    public abstract void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    public abstract void handle(User user) throws Exception;

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        UserConnection connection = Utils.getConnection(handler);
        Optional<User> optionalUser = BUCore.getApi().getUser(connection.getName());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            handle(user);
        }
    }
}