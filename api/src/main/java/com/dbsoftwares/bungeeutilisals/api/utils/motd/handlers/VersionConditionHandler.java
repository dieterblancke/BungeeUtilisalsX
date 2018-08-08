package com.dbsoftwares.bungeeutilisals.api.utils.motd.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.motd.ConditionHandler;
import net.md_5.bungee.api.connection.PendingConnection;

import java.util.logging.Level;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
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
            BUCore.log(Level.WARNING, "Found an invalid version in condition 'version " + condition + "'!");
            BUCore.log(Level.WARNING, "Available versions:");
            BUCore.log(listVersions());
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