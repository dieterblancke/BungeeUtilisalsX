package com.dbsoftwares.bungeeutilisals.bungee.api.tools;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.tools.ILoggers;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocations;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

public class Loggers implements ILoggers {

    File logFolder;
    LinkedList<Logger> loggers;

    public Loggers(BungeeUtilisals plugin) {
        logFolder = new File(plugin.getDataFolder(), "logs");
        loggers = Lists.newLinkedList();
        YamlConfiguration config = BungeeUtilisals.getConfigurations().get(FileLocations.CONFIG);

        createLogger("default", new File(logFolder, "default"),
                TimeUnit.valueOf(config.getString("logging.unit")),
                config.getInteger("logging.amount"));
    }

    @Override
    public LinkedList<Logger> getLoggers() {
        return loggers;
    }

    @Override
    public Logger createLogger(String name, File logFolder, TimeUnit expireUnit, int expireAmount) {

        Logger logger = new Logger(name, logFolder, expireUnit, expireAmount) {

            @Override
            public void log(LogLevel level, String message, String... replacements) {
                try {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    logWriter.write("[" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" +
                            calendar.get(Calendar.SECOND) + "] (" + level.toString() + ") " + String.format(message, (Object[]) replacements));
                    logWriter.newLine();
                    logWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };

        loggers.add(logger);
        return logger;
    }

    @Override
    public Logger getLogger(String name) {
        return loggers.stream().filter(logger -> logger.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Logger getDefaultLogger() {
        return getLogger("default");
    }
}