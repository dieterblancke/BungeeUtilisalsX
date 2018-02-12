package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.SQLStatements;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public boolean isBanned(UUID uuid) {
        return SQLStatements.isBanPresent(uuid, true);
    }

    @Override
    public boolean isTempBanned(UUID uuid) {
        return SQLStatements.isTempBanPresent(uuid, true);
    }

    @Override
    public boolean isIPBanned(String IP) {
        return SQLStatements.isIPBanPresent(IP, true);
    }

    @Override
    public boolean isIPTempBanned(String IP) {
        return SQLStatements.isIPTempBanPresent(IP, true);
    }

    @Override
    public boolean isMuted(UUID uuid) {
        return SQLStatements.isMutePresent(uuid, true);
    }

    @Override
    public boolean isTempMuted(UUID uuid) {
        return SQLStatements.isTempMutePresent(uuid, true);
    }

    @Override
    public boolean isIPMuted(String IP) {
        return SQLStatements.isIPMutePresent(IP, true);
    }

    @Override
    public boolean isIPTempMuted(String IP) {
        return SQLStatements.isIPTempMutePresent(IP, true);
    }

    @Override
    public PunishmentInfo addBan(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return SQLStatements.insertIntoBans(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addTempBan(UUID uuid, String name, String IP, long removeTime, String reason, String server, String executor) {
        return SQLStatements.insertIntoTempBans(uuid.toString(), name, IP, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPBan(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return SQLStatements.insertIntoIPBans(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return SQLStatements.insertIntoIPTempBans(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addMute(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return SQLStatements.insertIntoMutes(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return SQLStatements.insertIntoTempMutes(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPMute(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return SQLStatements.insertIntoIPMutes(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return SQLStatements.insertIntoIPTempMutes(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addKick(UUID uuid, String user, String ip, String reason, String server, String executor) {
        return null;
    }

    @Override
    public PunishmentInfo addWarn(UUID uuid, String user, String ip, String reason, String server, String executor) {
        return null;
    }

    @Override
    public PunishmentInfo getBan(UUID uuid) {
        return SQLStatements.getBan(uuid);
    }

    @Override
    public PunishmentInfo getTempBan(UUID uuid) {
        return SQLStatements.getTempBan(uuid);
    }

    @Override
    public PunishmentInfo getIPBan(String IP) {
        return SQLStatements.getIPBan(IP);
    }

    @Override
    public PunishmentInfo getIPTempBan(String IP) {
        return SQLStatements.getIPTempBan(IP);
    }

    @Override
    public PunishmentInfo getMute(UUID uuid) {
        return SQLStatements.getMute(uuid);
    }

    @Override
    public PunishmentInfo getTempMute(UUID uuid) {
        return SQLStatements.getTempMute(uuid);
    }

    @Override
    public PunishmentInfo getIPMute(String IP) {
        return SQLStatements.getIPMute(IP);
    }

    @Override
    public PunishmentInfo getIPTempMute(String IP) {
        return SQLStatements.getIPTempMute(IP);
    }

    @Override
    public void removeBan(UUID uuid) {
        SQLStatements.removeBan(uuid);
    }

    @Override
    public void removeTempBan(UUID uuid) {
        SQLStatements.removeTempBan(uuid);
    }

    @Override
    public void removeIPBan(String IP) {
        SQLStatements.removeIPBan(IP);
    }

    @Override
    public void removeIPTempBan(String IP) {
        SQLStatements.removeIPTempBan(IP);
    }

    @Override
    public void removeMute(UUID uuid) {
        SQLStatements.removeMute(uuid);
    }

    @Override
    public void removeTempMute(UUID uuid) {
        SQLStatements.removeTempMute(uuid);
    }

    @Override
    public void removeIPMute(String IP) {
        SQLStatements.removeIPMute(IP);
    }

    @Override
    public void removeIPTempMute(String IP) {
        SQLStatements.removeIPTempMute(IP);
    }

    @Override
    public String getDateFormat() {
        return BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("date-format");
    }

    @Override
    public String setPlaceHolders(String line, PunishmentInfo info) {
        line = line.replace("{reason}", info.getReason());
        line = line.replace("{date}", Utils.formatDate(getDateFormat(), info.getDate()));
        line = line.replace("{by}", info.getExecutedBy());
        line = line.replace("{server}", info.getServer());

        // Just adding in case someone wants them ...
        line = line.replace("{uuid}", info.getUuid().toString());
        line = line.replace("{ip}", info.getIP());
        line = line.replace("{user}", info.getUser());
        line = line.replace("{id}", String.valueOf(info.getId()));

        // Checking if value is present, if so: replacing
        if (info.getExpireTime() != null) {
            line = line.replace("{expire}", Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        }
        return line;
    }

    @Override
    public List<String> getPlaceHolders(PunishmentInfo info) {
        List<String> placeholders = Lists.newArrayList();

        placeholders.add("{reason}");
        placeholders.add(info.getReason());
        placeholders.add("{date}");
        placeholders.add(Utils.formatDate(getDateFormat(), info.getDate()));
        placeholders.add("{by}");
        placeholders.add(info.getExecutedBy());
        placeholders.add("{server}");
        placeholders.add(info.getServer());

        // Just adding in case someone wants them ...
        placeholders.add("{uuid}");
        placeholders.add(info.getUuid().toString());
        placeholders.add("{ip}");
        placeholders.add(info.getIP());
        placeholders.add("{user}");
        placeholders.add(info.getUser());
        placeholders.add("{id}");
        placeholders.add(String.valueOf(info.getId()));

        // Checking if value is present);
        // placeholders.add(if so: replacing
        if (info.getExpireTime() != null) {
            placeholders.add("{expire}");
            placeholders.add(Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        }

        return placeholders;
    }
}