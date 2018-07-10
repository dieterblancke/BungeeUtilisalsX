package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;

public class TitleAnnouncer extends Announcer {

    public TitleAnnouncer() {
        super(AnnouncementType.TITLE);
    }

    @Override
    public void loadAnnouncements() {
        // TODO
    }
}