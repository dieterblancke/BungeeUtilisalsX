package com.dbsoftwares.bungeeutilisals.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class TitleAnnouncement extends Announcement {

    private boolean language;
    private String title, subtitle;
    private int fadeIn, stay, fadeOut;

    public TitleAnnouncement(boolean language, String title, String subtitle,
                             int fadeIn, int stay, int fadeOut, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.language = language;
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
            IConfiguration config = BUCore.getApi().getLanguageManager().getLanguageConfiguration(BungeeUtilisals.getInstance(), player);
            BungeeTitle bungeeTitle = new BungeeTitle();

            bungeeTitle.title(Utils.format(player, language ? config.getString(title) : title));
            bungeeTitle.subTitle(Utils.format(player, language ? config.getString(subtitle) : subtitle));
            bungeeTitle.fadeIn(fadeIn * 20);
            bungeeTitle.stay(stay * 20);
            bungeeTitle.fadeOut(fadeOut * 20);

            player.sendTitle(bungeeTitle);
        });
    }
}