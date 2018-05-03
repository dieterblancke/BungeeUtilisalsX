package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.Lists;
import lombok.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class ChatAnnouncement implements Announcement {

    private String[] messages;
    private String[] servers;
    private String receivePermission;

    private List<ServerInfo> serverInfoList = Lists.newArrayList();

    public ChatAnnouncement(String[] messages, String[] servers, String receivePermission) {
        this.messages = messages;
        this.servers = servers;
        this.receivePermission = receivePermission;

        for (String server : servers) {
            if (server.equalsIgnoreCase("ALL")) {
                serverInfoList = null;
                break;
            } else {
                ServerInfo info = BungeeCord.getInstance().getServerInfo(server);

                if (info != null) {
                    serverInfoList.add(info);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChatAnnouncement announcement = (ChatAnnouncement) o;
        return Arrays.equals(messages, announcement.messages) && Arrays.equals(servers, announcement.servers)
                && receivePermission.equals(announcement.receivePermission);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(messages);
        result = 31 * result + Arrays.hashCode(servers);
        result = 31 * result + receivePermission.hashCode();
        return result;
    }

    @Override
    public void send() {
        if (serverInfoList == null || serverInfoList.isEmpty()) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverInfoList.forEach(server -> send(filter(server.getPlayers().stream())));
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> {
            for (String message : messages) {
                player.sendMessage(Utils.format(message));
            }
        });
    }

    private Stream<ProxiedPlayer> filter(Stream<ProxiedPlayer> stream) {
        return receivePermission.isEmpty() ? stream : stream.filter(player -> player.hasPermission(receivePermission));
    }
}