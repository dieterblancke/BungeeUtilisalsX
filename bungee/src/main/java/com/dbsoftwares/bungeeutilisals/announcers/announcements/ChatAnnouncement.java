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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ChatAnnouncement extends Announcement {

    private boolean prefix;
    private String languagePath;
    private List<String> messages;

    public ChatAnnouncement(boolean prefix, String languagePath, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.prefix = prefix;
        this.languagePath = languagePath;
    }

    public ChatAnnouncement(boolean prefix, List<String> messages, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.prefix = prefix;
        this.messages = messages;
    }

    @Override
    public void send() {
        if (serverGroup.isGlobal()) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverGroup.getServerInfos().forEach(server -> send(filter(server.getPlayers().stream())));
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> {
            IConfiguration config = BUCore.getApi().getLanguageManager()
                    .getLanguageConfiguration(BungeeUtilisals.getInstance().getDescription().getName(), player);

            final List<String> messageList;
            if (languagePath != null) {
                messageList = config.getStringList(languagePath);
            } else {
                messageList = messages;
            }
            for (String message : messageList) {
                if (prefix) {
                    message = config.getString("prefix") + " " + message;
                }
                player.sendMessage(Utils.format(player, message));
            }
        });
    }
}