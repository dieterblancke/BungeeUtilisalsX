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
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Data
public class TitleAnnouncement implements Announcement {

    private String[] servers;
    private String receivePermission;
    private Title title;

    private List<ServerInfo> serverInfoList = Lists.newArrayList();

    public TitleAnnouncement(String title, String subtitle, int fadein, int stay, int fadeout, String[] servers, String receivePermission) {
        this.servers = servers;
        this.receivePermission = receivePermission;

        this.title = new BungeeTitle();
        this.title.title(Utils.format(title));
        this.title.subTitle(Utils.format(subtitle));

        this.title.fadeIn(fadein);
        this.title.stay(stay);
        this.title.fadeOut(fadeout);

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

        TitleAnnouncement announcement = (TitleAnnouncement) o;
        return title.equals(announcement.title) && Arrays.equals(servers, announcement.servers)
                && receivePermission.equals(announcement.receivePermission);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + Arrays.hashCode(servers);
        result = 31 * result + receivePermission.hashCode();
        return result;
    }

    public void send() {
        if (serverInfoList == null) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverInfoList.forEach(server -> send(filter(server.getPlayers().stream())));
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> player.sendTitle(title));
    }

    private Stream<ProxiedPlayer> filter(Stream<ProxiedPlayer> stream) {
        return receivePermission.isEmpty() ? stream : stream.filter(player -> player.hasPermission(receivePermission));
    }
}