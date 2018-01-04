package com.dbsoftwares.bungeeutilisals.api.experimental.packets;

/*
 * Created by DBSoftwares on 26 december 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.Optional;

public abstract class Packet extends DefinedPacket {

    int id;

    public Packet(int id) {
        this.id = id;
    }

    @Override
    public abstract void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    @Override
    public abstract void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion);

    public abstract void handle(User user) throws Exception;

    @Override
    public int hashCode() {
        return 0;
    }

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