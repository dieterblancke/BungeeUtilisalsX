package com.dbsoftwares.bungeeutilisals.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.announcers.announcements.TitleAnnouncement;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.announcers.announcements.TitleAnnouncement;
import com.dbsoftwares.configuration.api.ISection;

public class TitleAnnouncer extends Announcer {

    public TitleAnnouncer() {
        super(AnnouncementType.TITLE);
    }

    @Override
    public void loadAnnouncements() {
        for (ISection section : configuration.getSectionList("announcements")) {
            ServerGroup group = FileLocation.SERVERGROUPS.getData(section.getString("server"));

            if (group == null) {
                BUCore.log("Could not find a servergroup or -name for " + section.getString("server") + "!");
                return;
            }

            int fadeIn = section.getInteger("fadein");
            int stay = section.getInteger("stay");
            int fadeOut = section.getInteger("fadeout");
            String permission = section.getString("permission");
            boolean language = section.getBoolean("language");
            String title = section.getString("title");
            String subtitle = section.getString("subtitle");

            addAnnouncement(new TitleAnnouncement(language, title, subtitle, fadeIn, stay, fadeOut, group, permission));
        }
    }
}