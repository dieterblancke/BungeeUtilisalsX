package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishmentts.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishmentts.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishmentts.PunishmentType;

import java.util.LinkedHashMap;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public void addPunishment(String user, PunishmentType type, PunishmentInfo info) {

    }

    @Override
    public void removePunishment(String user, PunishmentType type, String remover) {

    }

    @Override
    public Boolean isPunished(String user, PunishmentType type) {
        return null;
    }

    @Override
    public Boolean punishedBefore(String user, PunishmentType type) {
        return null;
    }

    @Override
    public LinkedHashMap<PunishmentInfo, String> getPunishments(String user, PunishmentType type) {
        return null;
    }

    @Override
    public LinkedHashMap<PunishmentInfo, String> getPunishments(String user) {
        return null;
    }

    @Override
    public PunishmentInfo getCurrentPunishment(String user, PunishmentType type) {
        return null;
    }
}