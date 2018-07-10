package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.ChatAnnouncement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class ChatAnnouncer extends Announcer {

    public ChatAnnouncer() {
        super(AnnouncementType.CHAT);
    }

    @Override
    public void loadAnnouncements() {
        for (ISection section : configuration.getSectionList("announcements")) {
            ServerGroup group = FileLocation.SERVERGROUPS.getData(section.getString("server"));

            if (group == null) {
                BUCore.log("Could not find a servergroup or -name for " + section.getString("server") + "!");
                return;
            }

            boolean usePrefix = section.getBoolean("use-prefix");
            String permission = section.getString("permission");

            if (section.isList("messages")) {
                List<String> messages = section.getStringList("messages");

                addAnnouncement(new ChatAnnouncement(usePrefix, messages, group, permission));
            } else {
                String messagePath = section.getString("messages");

                addAnnouncement(new ChatAnnouncement(usePrefix, messagePath, group, permission));
            }
        }
    }
}