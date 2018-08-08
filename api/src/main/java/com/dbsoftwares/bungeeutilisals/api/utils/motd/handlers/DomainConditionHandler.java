package com.dbsoftwares.bungeeutilisals.api.utils.motd.handlers;

import com.dbsoftwares.bungeeutilisals.api.utils.motd.ConditionHandler;
import net.md_5.bungee.api.connection.PendingConnection;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class DomainConditionHandler extends ConditionHandler {

    public DomainConditionHandler(String condition) {
        super(condition.replaceFirst("domain ", ""));
    }

    @Override
    public boolean checkCondition(PendingConnection connection) {
        if (connection.getVirtualHost() == null || connection.getVirtualHost().getHostName() == null) {
            return false;
        }
        String[] args = condition.split(" ");
        String operator = args[0];
        String conditionHost = args[1];
        String joinedHost = connection.getVirtualHost().getHostName();

        return operator.equalsIgnoreCase("==") == joinedHost.equalsIgnoreCase(conditionHost);
    }
}
