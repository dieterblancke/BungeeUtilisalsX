package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.TitleAnnouncement;

import java.util.LinkedHashMap;
import java.util.List;

public class TitleAnnouncer extends Announcer {

    public TitleAnnouncer() {
        super(AnnouncementType.TITLE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAnnouncements() {
        List<LinkedHashMap<String, Object>> announcements = configuration.getList("announcements");

        announcements.forEach(map -> {
            String title = (String) map.get("title");
            String subtitle = (String) map.get("subtitle");
            String[] servers = ((String) map.get("servers")).split(", ");
            String permission = (String) map.get("permission");
            int fadein = (int) map.get("fadein");
            int stay = (int) map.get("stay");
            int fadeout = (int) map.get("fadeout");

            addAnnouncement(new TitleAnnouncement(title, subtitle, fadein, stay, fadeout, servers, permission));
        });
    }
}