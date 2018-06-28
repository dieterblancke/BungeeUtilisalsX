package com.dbsoftwares.bungeeutilisals.bungee.announcers;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.bungee.announcers.announcements.BossBarAnnouncement;

import java.util.LinkedHashMap;
import java.util.List;

public class BossBarAnnouncer extends Announcer {

    public BossBarAnnouncer() {
        super(AnnouncementType.BOSSBAR);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadAnnouncements() {
        List<LinkedHashMap<String, Object>> announcements = configuration.getList("announcements");
        IBossBar bossBar = BUCore.getApi().createBossBar();

        announcements.forEach(map -> {
            String[] servers = ((String) map.get("servers")).split(", ");
            String permission = (String) map.get("permission");

            String text = (String) map.get("text");
            BarColor color = BarColor.valueOf(((String) map.get("color")).toUpperCase());
            BarStyle style = BarStyle.valueOf(((String) map.get("style")).toUpperCase());
            Double progress = (double) map.get("progress");
            TimeUnit stayUnit = TimeUnit.isUnit((String) map.get("stayunit"))
                    ? TimeUnit.valueOf(map.get("stayunit").toString().toUpperCase()) : TimeUnit.SECONDS;
            int stayTime = (int) map.get("staytime");

            addAnnouncement(new BossBarAnnouncement(bossBar, text, color, style, progress.floatValue(), stayUnit, stayTime, servers, permission));
        });
    }
}