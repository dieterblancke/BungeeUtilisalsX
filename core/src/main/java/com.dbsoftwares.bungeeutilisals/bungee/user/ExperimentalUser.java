package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.IExperimentalUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExperimentalUser implements IExperimentalUser {

    private final BUser user;

    @Override
    public void sendPacket(Packet packet) {
        user.getParent().unsafe().sendPacket(packet);
    }
}