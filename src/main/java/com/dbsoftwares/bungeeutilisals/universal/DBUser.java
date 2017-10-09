package com.dbsoftwares.bungeeutilisals.universal;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.google.common.collect.Maps;
import java.util.Map;

public interface DBUser {

    Map<String, DBUser> users = Maps.newHashMap();

    static DBUser searchUser(String user) {
        return users.getOrDefault(user.toLowerCase(), null);
    }

    static DBUser getConsole() {
        return users.get("CONSOLE");
    }

    Boolean isUser();

    Boolean isConsole();

    Boolean hasPermission(String permission);
}