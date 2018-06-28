package com.dbsoftwares.bungeeutilisals.api.announcer;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.math.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Announcer {

    protected IConfiguration configuration;

    private ScheduledTask task;

    @Getter
    private AnnouncementType type;
    @Getter
    private Map<Announcement, Boolean> announcements = Maps.newHashMap();
    private Iterator<Announcement> announcementIterator;

    @Getter
    private boolean enabled;
    @Getter
    private TimeUnit unit;
    @Getter
    private int delay;
    @Getter
    private boolean random;

    public Announcer(AnnouncementType type) {
        this.type = type;

        File folder = new File(BUCore.getApi().getPlugin().getDataFolder(), "announcer");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, type.toString().toLowerCase() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configuration = IConfiguration.loadYamlConfiguration(file);
        try {
            configuration.copyDefaults(IConfiguration.loadYamlConfiguration(
                    BUCore.getApi().getPlugin().getResourceAsStream("announcer/" + type.toString().toLowerCase() + ".yml")
            ));
        } catch (IOException e) {
            System.out.println("Could not load file defaults for " + type.toString().toLowerCase() + ".yml");
            e.printStackTrace();
        }

        enabled = configuration.getBoolean("enabled");
        unit = TimeUnit.isUnit(configuration.getString("delay.unit"))
                ? TimeUnit.valueOf(configuration.getString("delay.unit").toUpperCase()) : TimeUnit.SECONDS;
        delay = configuration.getInteger("delay.time");
        random = configuration.getBoolean("random");
    }

    public abstract void loadAnnouncements();

    public void start() {
        if (task != null) {
            throw new IllegalStateException("Announcer is already running.");
        }

        task = BungeeCord.getInstance().getScheduler().schedule(BUCore.getApi().getPlugin(), () -> {
            Announcement announcement;
            if (random) {
                announcement = getRandomAnnouncement();
            } else {
                if (announcementIterator == null || !announcementIterator.hasNext()) {
                    announcementIterator = announcements.keySet().iterator();
                }
                announcement = announcementIterator.next();
            }

            announcement.send();
        }, delay, delay, unit.toJavaTimeUnit());
    }

    public void stop() {
        if (task == null) {
            throw new IllegalStateException("Announcer is not running.");
        }
        task.cancel();
        task = null;
    }

    public void addAnnouncement(Announcement announcement) {
        announcements.put(announcement, false);
    }

    public Announcement getRandomAnnouncement() {
        if (!announcements.containsValue(false)) { // finished Announcement rotation, restarting it
            announcements.replaceAll((key, value) -> false);
        }

        List<Announcement> announcementsKeys = Lists.newArrayList();

        announcements.forEach((key, value) -> {
            if (!value) {
                announcementsKeys.add(key);
            }
        });

        Announcement random = MathUtils.getRandomFromList(announcementsKeys);
        announcements.put(random, true);
        return random;
    }
}