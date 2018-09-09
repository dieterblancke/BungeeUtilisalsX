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
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@Data
public class BossBarAnnouncement extends Announcement {

    private TimeUnit stayUnit;
    private int stayTime;

    private List<BossBarMessage> messages;
    private List<IBossBar> bars = Lists.newArrayList();

    private ScheduledTask task;

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
            task = ProxyServer.getInstance().getScheduler().schedule(BungeeUtilisals.getInstance(),
                    this::clear, stayTime, stayUnit.toJavaTimeUnit());
        }
    }

    @Override
    public void clear() {
        bars.forEach(bar -> {
            bar.clearUsers();
            bar.unregister();
        });
        if (task != null) { // for if stay > the announcement rotation delay (avoiding useless method calling)
            task.cancel();
            task = null;
        }
    }
}