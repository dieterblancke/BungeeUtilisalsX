package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class TitleAnnouncement extends Announcement {

    private String title, subtitle;
    private int fadeIn, stay, fadeOut;

    public TitleAnnouncement(String title, String subtitle, int fadeIn, int stay, int fadeOut, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void send() {
        if (serverGroup.isGlobal()) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverGroup.getServerInfos().forEach(server -> send(filter(server.getPlayers().stream())));
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> {
            BungeeTitle bungeeTitle = new BungeeTitle();

            // TODO

            player.sendTitle(bungeeTitle);
        });
    }
}