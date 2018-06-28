package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import lombok.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class ActionBarAnnouncement implements Announcement {

    private String message;
    private int time;
    private String[] servers;
    private String receivePermission;

    private ScheduledTask task;

    private List<ServerInfo> serverInfoList = Lists.newArrayList();

    public ActionBarAnnouncement(String message, String[] servers, int time, String receivePermission) {
        this.message = message;
        this.servers = servers;
        this.time = time;
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

        ActionBarAnnouncement announcement = (ActionBarAnnouncement) o;
        return message.equals(announcement.message) && Arrays.equals(servers, announcement.servers)
                && receivePermission.equals(announcement.receivePermission);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + message.hashCode();
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

        if (time > 1) {
            task = BungeeCord.getInstance().getScheduler().schedule(BungeeUtilisals.getInstance(), new Runnable() {

                private int count;

                @Override
                public void run() {
                    count++;

                    if (count > time) {
                        cancel();
                        return;
                    }
                    if (serverInfoList == null || serverInfoList.isEmpty()) {
                        send(filter(ProxyServer.getInstance().getPlayers().stream()));
                    } else {
                        serverInfoList.forEach(server -> send(filter(server.getPlayers().stream())));
                    }
                }
            }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    private void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> player.sendMessage(ChatMessageType.ACTION_BAR, Utils.format(message)));
    }

    private Stream<ProxiedPlayer> filter(Stream<ProxiedPlayer> stream) {
        return receivePermission.isEmpty() ? stream : stream.filter(player -> player.hasPermission(receivePermission));
    }
}