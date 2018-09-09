/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.announcers.announcements;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
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