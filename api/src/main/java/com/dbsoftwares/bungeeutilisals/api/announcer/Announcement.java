package com.dbsoftwares.bungeeutilisals.api.announcer;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

@Data
public abstract class Announcement {

    protected ServerGroup serverGroup;
    protected String receivePermission;

    protected Announcement(ServerGroup serverGroup, String receivePermission) {
        this.serverGroup = serverGroup;
        this.receivePermission = receivePermission;
    }

    public abstract void send();

    protected Stream<ProxiedPlayer> filter(Stream<ProxiedPlayer> stream) {
        return receivePermission.isEmpty() ? stream : stream.filter(player -> player.hasPermission(receivePermission));
    }

    public void clear() {}
}