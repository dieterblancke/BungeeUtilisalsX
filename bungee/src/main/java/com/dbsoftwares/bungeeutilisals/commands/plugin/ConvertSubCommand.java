package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class ConvertSubCommand extends SubCommand {

    public ConvertSubCommand() {
        super("convert");
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals convert (oldtype) [properties]";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.convert";
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 1 || args.length == 2) {
            // TODO: MAKE CONVERTORS (from MySQL to SQLite, SQLite to MySQL, ...)
            final String oldtype = args[0];
            final Map<String, String> properties = Maps.newHashMap();

            if (args.length == 2) {
                for (String property : args[1].split(",")) {
                    properties.put(property.split(":")[0], property.split(":")[1]);
                }
            }


        }
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
