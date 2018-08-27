package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.importer.Importer;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.importer.importers.BungeeUtilisalsImporter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class ImportSubCommand extends SubCommand {

    public ImportSubCommand() {
        super("import");
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals import (plugin) (properties)";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.reload";
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 2) {
            // TODO: testing + MAKE CONVERTORS (from MySQL to SQLite, SQLite to MySQL, ...)
            // properties example: host:host,port:port,database:database,username:username,password:password
            final String plugin = args[0];
            final Map<String, String> properties = Maps.newHashMap();

            for (String property : args[1].split(",")) {
                properties.put(property.split(":")[0], property.split(":")[1]);
            }

            if (plugin.equalsIgnoreCase("BungeeUtilisals")) {
                Importer importer = new BungeeUtilisalsImporter();

                importer.startImport(new ImporterCallback<Importer.ImporterStatus>() {
                    @Override
                    public void onStatusUpdate(Importer.ImporterStatus status) {
                        if (status.getConvertedEntries() % 100 == 0) {
                            BUCore.log(
                                    Level.INFO,
                                    "Converted " + status.getConvertedEntries() + " out of " + status.getTotalEntries()
                                            + " entries (" + status.getProgressionPercent() + " %)"
                            );
                        }
                    }

                    @Override
                    public void done(Importer.ImporterStatus status, Throwable throwable) {
                        BUCore.log(
                                Level.INFO,
                                "Finished converting " + status.getConvertedEntries() + " out of " + status.getTotalEntries()
                                        + ". " + status.getRemainingEntries() + " could not be converted ("
                                        + status.getProgressionPercent() + " %)"
                        );
                    }
                }, properties);
            }
        }
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
