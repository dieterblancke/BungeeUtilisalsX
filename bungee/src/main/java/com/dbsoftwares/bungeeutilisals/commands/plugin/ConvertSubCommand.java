/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

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
