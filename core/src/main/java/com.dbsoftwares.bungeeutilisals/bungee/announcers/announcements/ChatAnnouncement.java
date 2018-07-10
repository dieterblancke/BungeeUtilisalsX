package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
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
                    .getLanguageConfiguration(BungeeUtilisals.getInstance(), player);

            List<String> messages;
            if (languagePath != null) {
                messages = config.getStringList(languagePath);
            } else {
                messages = this.messages;
            }
            for (String message : messages) {
                if (prefix) {
                    message = config.getString("prefix") + " " + message;
                }
                player.sendMessage(Utils.format(message));
            }
        });
    }
}