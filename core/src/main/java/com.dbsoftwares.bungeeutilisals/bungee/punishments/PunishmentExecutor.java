package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.mysql.MySQL;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentTable;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.bungee.tables.*;
import com.google.common.collect.Lists;

import java.util.LinkedList;

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
    public void removePunishment(boolean uuid, String user, PunishmentType type, String removedby) {
        if (!type.isRemovable()) {
            return;
        }
        Object instance = type.newInstance();
        type.setRemovedBy(instance, removedby);
        type.setActive(instance, false);

        MySQL.update(instance, (uuid ? "uuid" : "user") + " = %s", user);
    }

    @Override
    public Boolean hasActivePunishment(boolean uuid, String user, PunishmentType type) {
        // Kicks & Warns can only be in the past, cannot be active.
        return type.isRemovable() && MySQL.search(type.getTable()).select("id")
                .where((uuid ? "uuid" : "user") + " = %s AND active = %s", user, true).search().isPresent();
    }

    @Override
    public Boolean hasPunishment(boolean uuid, String user, PunishmentType type) {
        return MySQL.search(type.getTable()).select("id")
                .where((uuid ? "uuid" : "user") + " = %s", user).search().isPresent();
    }

    @Override
    public LinkedList<? extends PunishmentTable> getPunishments(boolean uuid, String user, PunishmentType type) {
        return MySQL.search(type.getTable()).select("*")
                .where((uuid ? "uuid" : "user") + " = %s", user).search().multiGet();
    }

    @Override
    public LinkedList<? extends PunishmentTable> getPunishments(boolean uuid, String user) {
        LinkedList<PunishmentTable> punishments = Lists.newLinkedList();

        for (PunishmentType type : PunishmentType.values()) {
            punishments.addAll(getPunishments(uuid, user, type));
        }

        return punishments;
    }

    @Override
    public PunishmentTable getCurrentPunishment(boolean uuid, String user, PunishmentType type) {
        if (!type.isRemovable()) {
            return null;
        }
        return MySQL.search(type.getTable()).select("id")
                .where((uuid ? "uuid" : "user") + " = %s", user).search().get();
    }
}