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

package com.dbsoftwares.bungeeutilisals.announcers;

import com.dbsoftwares.bungeeutilisals.announcers.announcements.BossBarAnnouncement;
import com.dbsoftwares.bungeeutilisals.announcers.announcements.BossBarMessage;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.List;

public class BossBarAnnouncer extends Announcer {

    public BossBarAnnouncer() {
        super(AnnouncementType.BOSSBAR);
    }

    @Override
    public void loadAnnouncements() {
        for (ISection section : configuration.getSectionList("announcements")) {
            ServerGroup group = FileLocation.SERVERGROUPS.getData(section.getString("server"));

            if (group == null) {
                BUCore.log("Could not find a servergroup or -name for " + section.getString("server") + "!");
                return;
            }
            List<BossBarMessage> messages = Lists.newArrayList();

            int time;
            TimeUnit unit;
            if (section.isInteger("stay")) {
                unit = TimeUnit.SECONDS;
                time = section.getInteger("stay");
            } else {
                unit = TimeUnit.valueOfOrElse(section.getString("stay.unit"), TimeUnit.SECONDS);
                time = section.getInteger("stay.time");
            }
            String permission = section.getString("permission");

            for (ISection message : section.getSectionList("messages")) {
                messages.add(
                        new BossBarMessage(
                                BarColor.valueOf(message.getString("color")),
                                BarStyle.valueOf(message.getString("style")),
                                message.getFloat("progress"),
                                message.getBoolean("language"),
                                message.getString("text")
                        )
                );
            }
            addAnnouncement(new BossBarAnnouncement(messages, unit, time, group, permission));
        }
    }

    @Override
    public void stop() {
        super.stop();
        super.getAnnouncements().keySet().forEach(Announcement::clear);
    }
}