package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.google.common.collect.Lists;
import lombok.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class BossBarAnnouncement implements Announcement {

    private String[] servers;
    private String receivePermission;
    private String text;
    private BarColor color;
    private BarStyle style;
    private float progress;
    private TimeUnit stayUnit;
    private int stayTime;

    private IBossBar bossBar;

    private List<ServerInfo> serverInfoList = Lists.newArrayList();

    public BossBarAnnouncement(IBossBar bossBar, String text, BarColor color, BarStyle style, float progress,
                               TimeUnit stayUnit, int stayTime, String[] servers, String receivePermission) {
        this.bossBar = bossBar;
        this.text = text;
        this.color = color;
        this.style = style;
        this.progress = progress;
        this.stayUnit = stayUnit;
        this.stayTime = stayTime;
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

    public void send() {
        bossBar.clearUsers();

        BaseComponent[] formatted = Utils.format(text);
        bossBar.setMessage(formatted);
        bossBar.setColor(color);
        bossBar.setStyle(style);
        bossBar.setProgress(progress);

        if (serverInfoList == null) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverInfoList.forEach(server -> send(filter(server.getPlayers().stream())));
        }

        if (stayTime > 0) {
            BUCore.getApi().getSimpleExecutor().delayedExecute((int) stayUnit.toSeconds(stayTime), () -> {
                if (Arrays.equals(bossBar.getMessage(), formatted) && bossBar.getColor().equals(color) && bossBar.getStyle().equals(style)
                        && bossBar.getProgress() == progress) {
                    bossBar.clearUsers();
                }
            });
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> {
            Optional<User> optionalUser = BUCore.getApi().getUser(player);

            optionalUser.ifPresent(user -> bossBar.addUser(user));
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BossBarAnnouncement that = (BossBarAnnouncement) o;
        return Float.compare(that.progress, progress) == 0 &&
                stayTime == that.stayTime &&
                Arrays.equals(servers, that.servers) &&
                Objects.equals(receivePermission, that.receivePermission) &&
                Objects.equals(text, that.text) &&
                Objects.equals(color, that.color) &&
                Objects.equals(style, that.style) &&
                stayUnit == that.stayUnit &&
                Objects.equals(bossBar, that.bossBar) &&
                Objects.equals(serverInfoList, that.serverInfoList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(receivePermission, text, color, style, progress, stayUnit, stayTime, bossBar, serverInfoList);
        result = 31 * result + Arrays.hashCode(servers);
        return result;
    }

    private Stream<ProxiedPlayer> filter(Stream<ProxiedPlayer> stream) {
        return receivePermission.isEmpty() ? stream : stream.filter(player -> player.hasPermission(receivePermission));
    }
}