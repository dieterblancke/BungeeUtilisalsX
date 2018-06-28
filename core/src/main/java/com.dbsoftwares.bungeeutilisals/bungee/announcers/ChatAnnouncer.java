package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.ChatAnnouncement;

import java.util.LinkedHashMap;
import java.util.List;

public class ChatAnnouncer extends Announcer {

    public ChatAnnouncer() {
        super(AnnouncementType.CHAT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAnnouncements() {
        List<LinkedHashMap<String, Object>> announcements = configuration.getList("announcements");

        announcements.forEach(map -> {
            String[] messages = ((List<String>) map.get("messages")).toArray(new String[]{});
            String[] servers = ((String) map.get("servers")).split(", ");
            String permission = (String) map.get("permission");

            addAnnouncement(new ChatAnnouncement(messages, servers, permission));
        });
    }
}