package com.dbsoftwares.bungeeutilisals.api.utils.motd;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.connection.PendingConnection;

@Data
@AllArgsConstructor
public abstract class ConditionHandler {

    protected String condition;

    public abstract boolean checkCondition(PendingConnection connection);

}
