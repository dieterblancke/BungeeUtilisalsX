package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.ActionBarAnnouncement;

import java.util.LinkedHashMap;
import java.util.List;

public class ActionBarAnnouncer extends Announcer {

    public ActionBarAnnouncer() {
        super(AnnouncementType.ACTIONBAR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAnnouncements() {
        List<LinkedHashMap<String, Object>> announcements = configuration.getList("announcements");

        announcements.forEach(map -> {
            String text = (String) map.get("text");
            String[] servers = ((String) map.get("servers")).split(", ");
            String permission = (String) map.get("permission");
            int time = (int) map.get("time");

            addAnnouncement(new ActionBarAnnouncement(text, servers, time, permission));
        });
    }
}