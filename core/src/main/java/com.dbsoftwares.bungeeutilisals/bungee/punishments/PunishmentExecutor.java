package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.mysql.MySQL;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.bungee.tables.*;

import java.util.LinkedHashMap;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public void addPunishment(PunishmentType type, PunishmentInfo info) {
        Object table = null;
        switch (type) {
            case BAN: {
                table = BansTable.fromInfo(info);
                break;
            }
            case IPBAN: {
                table = IPBansTable.fromInfo(info);
                break;
            }
            case MUTE: {
                table = MutesTable.fromInfo(info);
                break;
            }
            case IPMUTE: {
                table = IPMutesTable.fromInfo(info);
                break;
            }
            case KICK: {
                table = KicksTable.fromInfo(info);
                break;
            }
            case WARN: {
                table = WarnsTable.fromInfo(info);
                break;
            }
        }
        if (table != null) {
            MySQL.insert(table);
        }
    }

    @Override
    public void addTemporaryPunishment(PunishmentType type, PunishmentInfo info, long expire) {
        Object table = null;
        switch (type) {
            case TEMPBAN: {
                table = TempBansTable.fromInfo(info);
                break;
            }
            case IPTEMPBAN: {
                table = IPTempBansTable.fromInfo(info);
                break;
            }
            case TEMPMUTE: {
                table = TempMutesTable.fromInfo(info);
                break;
            }
            case IPTEMPMUTE: {
                table = IPTempMutesTable.fromInfo(info);
                break;
            }
        }
        if (table != null) {
            MySQL.insert(table);
        }
    }

    @Override
    public void removePunishment(String user, PunishmentType type, String removedby) {

    }

    @Override
    public Boolean hasPunishment(boolean uuid, String user, PunishmentType type) {
        return null;
    }

    @Override
    public Boolean hasPastPunishment(boolean uuid, String user, PunishmentType type) {
        return null;
    }

    @Override
    public LinkedHashMap<PunishmentInfo, String> getPunishments(boolean uuid, String user, PunishmentType type) {
        return null;
    }

    @Override
    public LinkedHashMap<PunishmentInfo, String> getPunishments(boolean uuid, String user) {
        return null;
    }

    @Override
    public PunishmentInfo getCurrentPunishment(boolean uuid, String user, PunishmentType type) {
        return null;
    }
}