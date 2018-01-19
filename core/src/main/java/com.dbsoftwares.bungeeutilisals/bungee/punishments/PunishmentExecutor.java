package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.storage.SQLStatements;

import java.util.UUID;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public boolean isBanned(UUID uuid) {
        return SQLStatements.isBanPresent(uuid, true);
    }

    @Override
    public boolean isTempBanned(UUID uuid) {
        return false;
    }

    @Override
    public boolean isIPBanned(String IP) {
        return SQLStatements.isIPBanPresent(IP, true);
    }

    @Override
    public boolean isIPTempBanned(String IP) {
        return false;
    }

    @Override
    public boolean isMuted(UUID uuid) {
        return false;
    }

    @Override
    public boolean isTempMuted(UUID uuid) {
        return false;
    }

    @Override
    public boolean isIPMuted(String IP) {
        return false;
    }

    @Override
    public boolean isIPTempMuted(String IP) {
        return false;
    }

    @Override
    public void addBan(UUID uuid, String name, String IP, String reason, String server, String executor) {
        SQLStatements.insertIntoBans(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public void addTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addIPBan(UUID uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addIPTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addMute(UUID uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addIPMute(UUID uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addIPTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addKick(UUID uuid, String user, String ip, String reason, String server, String executor) {

    }

    @Override
    public void addWarn(UUID uuid, String user, String ip, String reason, String server, String executor) {

    }
}