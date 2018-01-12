package com.dbsoftwares.bungeeutilisals.api.punishments;

/*
 * Created by DBSoftwares on 12/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public interface PunishmentTable {

    PunishmentType getType();

    int getId();

    String getUuid();

    String getUser();

    String getIp();

    String getReason();

    String getServer();

    String getDate();

    Boolean isActive();

    String getExecutedby();

    String getRemovedby();

    Long getRemoveTime();
}