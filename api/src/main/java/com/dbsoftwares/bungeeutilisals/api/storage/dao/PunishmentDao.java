package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;

import java.util.Date;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public interface PunishmentDao {

    long getPunishmentsSince(String identifier, PunishmentType type, Date date);

    PunishmentInfo insertPunishment(
            PunishmentType type, UUID uuid, String user, String ip, String reason,
            Long time, String server, Boolean active, String executedby
    );

    PunishmentInfo insertPunishment(
            PunishmentType type, UUID uuid, String user, String ip, String reason,
            Long time, String server, Boolean active, String executedby, String removedby
    );

    boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive);

    PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP);

    void removePunishment(PunishmentType type, UUID uuid, String IP);
}
