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

package com.dbsoftwares.bungeeutilisals.api.motd.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import net.md_5.bungee.api.connection.PendingConnection;

public class VersionConditionHandler extends ConditionHandler {

    public VersionConditionHandler(String condition) {
        super(condition.replaceFirst("version ", ""));
    }

    @Override
    public boolean checkCondition(PendingConnection connection) {
        String[] args = condition.split(" ");
        String operator = args[0];
        Version version = formatVersion(args[1]);

        if (version == null) {
            return false;
        }

        switch (operator) {
            case "<":
                return connection.getVersion() < version.getVersion();
            case "<=":
                return connection.getVersion() <= version.getVersion();
            case "==":
                return connection.getVersion() == version.getVersion();
            case "!=":
                return connection.getVersion() != version.getVersion();
            case ">=":
                return connection.getVersion() >= version.getVersion();
            case ">":
                return connection.getVersion() > version.getVersion();
        }

        return false;
    }

    private Version formatVersion(String mcVersion) {
        try {
            return Version.valueOf("MINECRAFT_" + mcVersion.replace(".", "_"));
        } catch (IllegalArgumentException e) {
            BUCore.getLogger().warn("Found an invalid version in condition 'version " + condition + "'!");
            BUCore.getLogger().warn("Available versions:");
            BUCore.getLogger().warn(listVersions());
            return null;
        }
    }

    private String listVersions() {
        StringBuilder builder = new StringBuilder();
        int length = Version.values().length;

        for (int i = 0; i < length; i++) {
            Version version = Version.values()[i];

            builder.append(version.toString());

            if (i < length) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}