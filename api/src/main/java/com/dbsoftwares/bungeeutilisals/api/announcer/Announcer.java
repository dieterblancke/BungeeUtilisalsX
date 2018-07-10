package com.dbsoftwares.bungeeutilisals.api.announcer;

/*
 * Created by DBSoftwares on 02/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.math.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Data
public abstract class Announcer {

    @Getter
    private static Map<AnnouncementType, Announcer> announcers = Maps.newHashMap();

    protected IConfiguration configuration;
    private ScheduledTask task;
    private AnnouncementType type;
    private Map<Announcement, Boolean> announcements = Maps.newHashMap();
    private Iterator<Announcement> announcementIterator;
    private boolean enabled;
    private TimeUnit unit;
    private int delay;
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
                    BUCore.getApi().getPlugin().getResourceAsStream("announcers/" + type.toString().toLowerCase() + ".yml")
            ));
        } catch (IOException e) {
            BUCore.log("Could not load file defaults for " + type.toString().toLowerCase() + ".yml");
            e.printStackTrace();
        }

        enabled = configuration.getBoolean("enabled");
        unit = TimeUnit.valueOfOrElse(configuration.getString("delay.unit"), TimeUnit.SECONDS);
        delay = configuration.getInteger("delay.time");
        random = configuration.getBoolean("random");
    }

    @SafeVarargs
    public static void registerAnnouncers(Class<? extends Announcer>... classes) {
        for (Class<? extends Announcer> clazz : classes) {
            try {
                Announcer announcer = clazz.newInstance();

                if (announcer.isEnabled()) {
                    announcer.loadAnnouncements();
                    announcer.start();

                    BUCore.log("Loading " + announcer.getType().toString().toLowerCase() + " announcements ...");
                }

                announcers.put(announcer.getType(), announcer);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (task != null) {
            throw new IllegalStateException("Announcer is already running.");
        }

        task = BungeeCord.getInstance().getScheduler().schedule(
                BUCore.getApi().getPlugin(),
                new Runnable() {

                    private Announcement previous;

                    @Override
                    public void run() {
                        if (previous != null) {
                            previous.clear();
                        }
                        Announcement next = (random ? getRandomAnnouncement() : getNextAnnouncement());
                        next.send();
                        previous = next;
                    }
                },
                delay,
                delay,
                unit.toJavaTimeUnit()
        );
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

    private Announcement getRandomAnnouncement() {
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

    private Announcement getNextAnnouncement() {
        if (announcementIterator == null || !announcementIterator.hasNext()) {
            announcementIterator = announcements.keySet().iterator();
        }
        return announcementIterator.next();
    }

    public abstract void loadAnnouncements();
}