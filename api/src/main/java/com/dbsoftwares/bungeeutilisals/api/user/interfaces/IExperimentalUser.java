package com.dbsoftwares.bungeeutilisals.api.user.interfaces;

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;

public interface IExperimentalUser {

    /**
     * Sends a packet.
     * @param packet The packet to be sent.
     */
    void sendPacket(Packet packet);
}