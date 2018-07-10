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
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.ActionBarAnnouncement;

public class ActionBarAnnouncer extends Announcer {

    public ActionBarAnnouncer() {
        super(AnnouncementType.ACTIONBAR);
    }

    @Override
    public void loadAnnouncements() {
        for (ISection section : configuration.getSectionList("announcements")) {
            ServerGroup group = FileLocation.SERVERGROUPS.getData(section.getString("server"));

            if (group == null) {
                BUCore.log("Could not find a servergroup or -name for " + section.getString("server") + "!");
                return;
            }

            boolean useLanguage = section.getBoolean("use-language");
            int time = section.getInteger("time");
            String permission = section.getString("permission");

            String message = section.getString("message");

            addAnnouncement(new ActionBarAnnouncement(useLanguage, time, message, group, permission));
        }
    }
}