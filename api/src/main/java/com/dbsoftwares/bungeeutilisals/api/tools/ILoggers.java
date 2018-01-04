package com.dbsoftwares.bungeeutilisals.api.tools;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import lombok.Data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

public interface ILoggers {

    /**
     * @return A list of all registered Loggers.
     */
    LinkedList<Logger> getLoggers();

    /**
     * Registers a new loggers in the BungeeUtilisals system.
     *
     * @param name         The name of the logger.
     * @param logFolder    The folder in which the logs get stored.
     * @param expireUnit   The unit in which expire time will be calculated.
     * @param expireAmount The amount of time untill a logg will be deleted.
     * @return The logger you just created.
     */
    Logger createLogger(String name, File logFolder, TimeUnit expireUnit, int expireAmount);

    /**
     * @param name The name of the logger you want to get.
     * @return The logger bound to the name.
     */
    Logger getLogger(String name);

    /**
     * @return The default BungeeUtilisals logger.
     */
    Logger getDefaultLogger();

    enum LogLevel {
        INFO, WARNING, ERROR
    }

    @Data
    abstract class Logger {

        public final BufferedWriter logWriter;
        private final String name;
        private final File logFolder;
        private final TimeUnit expireUnit;
        private final int expireAmount;
        private final FileWriter fileWriter;

        public Logger(String name, File logFolder, TimeUnit expireUnit, int expireAmount) {
            this.name = name;
            this.logFolder = logFolder;
            this.expireUnit = expireUnit;
            this.expireAmount = expireAmount;

            if (logFolder.exists()) {
                File latest = new File(logFolder, "latest.log");

                if (latest.exists()) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                    File file = new File(logFolder, calendar.get(Calendar.DATE) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR) + ".log");
                    if (file.exists()) {
                        for (Integer i = 0; i < 1000; i++) {
                            file = new File(logFolder, calendar.get(Calendar.DATE) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR) + "-" + i + ".log");
                            if (!file.exists()) {
                                break;
                            }
                        }
                    }
                    latest.renameTo(file);
                } else {
                    try {
                        latest.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logFolder.mkdirs();
            }
            File latest = new File(logFolder, "latest.log");

            FileWriter fileWriter;
            BufferedWriter logWriter;
            try {
                fileWriter = new FileWriter(latest);
                logWriter = new BufferedWriter(fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
                fileWriter = null;
                logWriter = null;
            }
            this.fileWriter = fileWriter;
            this.logWriter = logWriter;
        }

        public abstract void log(LogLevel level, String message, String... replacements);

        public void shutdown() {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}