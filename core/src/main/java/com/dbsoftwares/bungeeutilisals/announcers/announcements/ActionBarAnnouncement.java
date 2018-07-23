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
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionBarAnnouncement extends Announcement {

    private boolean language;
    private int time;
    private String message;
    private ScheduledTask task;

    public ActionBarAnnouncement(boolean language, int time, String message, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.language = language;
        this.time = time;
        this.message = message;
    }

    @Override
    public void send() {
        if (serverGroup.isGlobal()) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverGroup.getServerInfos().forEach(server -> send(filter(server.getPlayers().stream())));
        }

        if (time > 1) {
            task = BungeeCord.getInstance().getScheduler().schedule(BungeeUtilisals.getInstance(), new Runnable() {

                private int count = 1;

                @Override
                public void run() {
                    count++;

                    if (count > time) {
                        task.cancel();
                        return;
                    }
                    if (serverGroup.isGlobal()) {
                        send(filter(ProxyServer.getInstance().getPlayers().stream()));
                    } else {
                        serverGroup.getServerInfos().forEach(server -> send(filter(server.getPlayers().stream())));
                    }
                }
            }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> {
            String bar = message;

            if (language) {
                bar = BUCore.getApi().getLanguageManager().getLanguageConfiguration(
                        BungeeUtilisals.getInstance(), player
                ).getString(message);
            }

            player.sendMessage(ChatMessageType.ACTION_BAR, Utils.format(player, bar));
        });
    }
}