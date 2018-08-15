package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;

import java.util.Date;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class SQLPunishmentDao implements PunishmentDao {

    // TODO

    @Override
    public long getPunishmentsSince(String identifier, PunishmentType type, Date date) {
        return 0;
    }

    @Override
    public PunishmentInfo insertPunishment(PunishmentType type, UUID uuid, String user, String ip, String reason, Long time, String server, Boolean active, String executedby) {
        return null;
    }

    @Override
    public boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive) {
        return false;
    }

    @Override
    public PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP) {
        return null;
    }

    @Override
    public void removePunishment(PunishmentType type, UUID uuid, String IP) {

    }
}
