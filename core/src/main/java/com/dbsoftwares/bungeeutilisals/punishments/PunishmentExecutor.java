package com.dbsoftwares.bungeeutilisals.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public boolean isBanned(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isBanPresent(uuid, true);
    }

    @Override
    public boolean isTempBanned(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isTempBanPresent(uuid, true);
    }

    @Override
    public boolean isIPBanned(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPBanPresent(IP, true);
    }

    @Override
    public boolean isIPTempBanned(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPTempBanPresent(IP, true);
    }

    @Override
    public boolean isMuted(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isMutePresent(uuid, true);
    }

    @Override
    public boolean isTempMuted(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isTempMutePresent(uuid, true);
    }

    @Override
    public boolean isIPMuted(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPMutePresent(IP, true);
    }

    @Override
    public boolean isIPTempMuted(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPTempMutePresent(IP, true);
    }

    @Override
    public PunishmentInfo addBan(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoBans(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addTempBan(UUID uuid, String name, String IP, long removeTime, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoTempBans(uuid.toString(), name, IP, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPBan(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoIPBans(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoIPTempBans(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addMute(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoMutes(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoTempMutes(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPMute(UUID uuid, String name, String IP, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoIPMutes(uuid.toString(), name, IP, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addIPTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoIPTempMutes(uuid.toString(), user, ip, removeTime, reason, server, true, executor);
    }

    @Override
    public PunishmentInfo addKick(UUID uuid, String user, String ip, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoKicks(uuid.toString(), user, ip, reason, server, executor);
    }

    @Override
    public PunishmentInfo addWarn(UUID uuid, String user, String ip, String reason, String server, String executor) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().insertIntoWarns(uuid.toString(), user, ip, reason, server, executor);
    }

    @Override
    public PunishmentInfo getBan(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getBan(uuid);
    }

    @Override
    public PunishmentInfo getTempBan(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getTempBan(uuid);
    }

    @Override
    public PunishmentInfo getIPBan(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getIPBan(IP);
    }

    @Override
    public PunishmentInfo getIPTempBan(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getIPTempBan(IP);
    }

    @Override
    public PunishmentInfo getMute(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getMute(uuid);
    }

    @Override
    public PunishmentInfo getTempMute(UUID uuid) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getTempMute(uuid);
    }

    @Override
    public PunishmentInfo getIPMute(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getIPMute(IP);
    }

    @Override
    public PunishmentInfo getIPTempMute(String IP) {
        return BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getIPTempMute(IP);
    }

    @Override
    public void removeBan(UUID uuid) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeBan(uuid);
    }

    @Override
    public void removeTempBan(UUID uuid) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeTempBan(uuid);
    }

    @Override
    public void removeIPBan(String IP) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeIPBan(IP);
    }

    @Override
    public void removeIPTempBan(String IP) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeIPTempBan(IP);
    }

    @Override
    public void removeMute(UUID uuid) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeMute(uuid);
    }

    @Override
    public void removeTempMute(UUID uuid) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeTempMute(uuid);
    }

    @Override
    public void removeIPMute(String IP) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeIPMute(IP);
    }

    @Override
    public void removeIPTempMute(String IP) {
        BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().removeIPTempMute(IP);
    }

    @Override
    public String getDateFormat() {
        return FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("date-format");
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