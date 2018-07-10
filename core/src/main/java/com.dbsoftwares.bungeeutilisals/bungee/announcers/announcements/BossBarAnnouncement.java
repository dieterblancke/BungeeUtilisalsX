package com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class BossBarAnnouncement extends Announcement {

    private TimeUnit stayUnit;
    private int stayTime;

    private List<BossBarMessage> messages;
    private List<IBossBar> bars;

    public BossBarAnnouncement(List<BossBarMessage> messages, TimeUnit stayUnit, int stayTime, ServerGroup serverGroup, String receivePermission) {
        super(serverGroup, receivePermission);

        this.messages = messages;
        this.stayUnit = stayUnit;
        this.stayTime = stayTime;
    }

    public void send() {
        if (serverGroup.isGlobal()) {
            send(filter(ProxyServer.getInstance().getPlayers().stream()));
        } else {
            serverGroup.getServerInfos().forEach(server -> send(filter(server.getPlayers().stream())));
        }
    }

    private void send(Stream<ProxiedPlayer> stream) {
        stream.forEach(player -> BUCore.getApi().getUser(player).ifPresent(user -> {
            IConfiguration config = user.getLanguageConfig();

            messages.forEach(message -> {
                IBossBar bar = BUCore.getApi().createBossBar();

                bar.setMessage(Utils.format(user, message.isLanguage()
                        ? config.getString(message.getText())
                        : message.getText()));
                bar.setColor(message.getColor());
                bar.setProgress(message.getProgress());
                bar.setStyle(message.getStyle());

                bar.addUser(user);

                bars.add(bar);
            });
        }));
        if (stayTime > 0) {
            ProxyServer.getInstance().getScheduler().schedule(BungeeUtilisals.getInstance(),
                    this::clear, stayTime, stayUnit.toJavaTimeUnit());
        }
    }

    @Override
    public void clear() {
        bars.forEach(bar -> {
            bar.clearUsers();
            bar.unregister();
        });
    }
}