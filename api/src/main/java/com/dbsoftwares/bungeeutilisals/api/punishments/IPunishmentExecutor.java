package com.dbsoftwares.bungeeutilisals.api.punishments;

import java.util.List;
import java.util.UUID;

public interface IPunishmentExecutor {

    boolean isBanned(UUID uuid);

    boolean isTempBanned(UUID uuid);

    boolean isIPBanned(String IP);

    boolean isIPTempBanned(String IP);

    boolean isMuted(UUID uuid);

    boolean isTempMuted(UUID uuid);

    boolean isIPMuted(String IP);

    boolean isIPTempMuted(String IP);

    PunishmentInfo addBan(UUID uuid, String name, String IP, String reason, String server, String executor);

    PunishmentInfo addTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    PunishmentInfo addIPBan(UUID uuid, String name, String IP, String reason, String server, String executor);

    PunishmentInfo addIPTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    PunishmentInfo addMute(UUID uuid, String name, String IP, String reason, String server, String executor);

    PunishmentInfo addTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    PunishmentInfo addIPMute(UUID uuid, String name, String IP, String reason, String server, String executor);

    PunishmentInfo addIPTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    PunishmentInfo addKick(UUID uuid, String user, String ip, String reason, String server, String executor);

    PunishmentInfo addWarn(UUID uuid, String user, String ip, String reason, String server, String executor);

    String getDateFormat();

    String setPlaceHolders(String line, PunishmentInfo info);

    List<String> getPlaceHolders(PunishmentInfo info);
}