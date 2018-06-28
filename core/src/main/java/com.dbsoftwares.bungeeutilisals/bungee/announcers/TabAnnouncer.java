package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.TabAnnouncement;

import java.util.LinkedHashMap;
import java.util.List;

public class TabAnnouncer extends Announcer {

    public TabAnnouncer() {
        super(AnnouncementType.TAB);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAnnouncements() {
        List<LinkedHashMap<String, Object>> announcements = configuration.getList("announcements");

        announcements.forEach(map -> {
            String header = (String) map.get("header");
            String footer = (String) map.get("footer");
            String[] servers = ((String) map.get("servers")).split(", ");
            String permission = (String) map.get("permission");

            addAnnouncement(new TabAnnouncement(header, footer, servers, permission));
        });
    }
}