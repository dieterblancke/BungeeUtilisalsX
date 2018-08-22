package com.dbsoftwares.bungeeutilisals.api.utils.motd.handlers;

import com.dbsoftwares.bungeeutilisals.api.utils.motd.ConditionHandler;
import net.md_5.bungee.api.connection.PendingConnection;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class NameConditionHandler extends ConditionHandler {

    public NameConditionHandler(String condition) {
        super(condition.replaceFirst("name ", ""));
    }

    @Override
    public boolean checkCondition(PendingConnection connection) {
        String[] args = condition.split(" ");
        String operator = args[0];
        String value = args[1];

        if (operator.equalsIgnoreCase("==")) {
            if (connection.getName() == null) {
                return value.equalsIgnoreCase("null");
            }
            return connection.getName().equalsIgnoreCase(value);
        } else if (operator.equalsIgnoreCase("!=")) {
            if (connection.getName() == null) {
                return !value.equalsIgnoreCase("null");
            }
            return !connection.getName().equalsIgnoreCase(value);
        }

        return false;
    }
}