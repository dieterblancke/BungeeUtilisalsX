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

import com.dbsoftwares.bungeeutilisals.api.motd.ConditionHandler;
import net.md_5.bungee.api.connection.PendingConnection;

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
