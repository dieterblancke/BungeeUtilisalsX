package com.dbsoftwares.bungeeutilisals.api.punishments;

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

    void addBan(UUID uuid, String name, String IP, String reason, String server, String executor);

    void addTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addIPBan(UUID uuid, String name, String IP, String reason, String server, String executor);

    void addIPTempBan(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addMute(UUID uuid, String name, String IP, String reason, String server, String executor);

    void addTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addIPMute(UUID uuid, String name, String IP, String reason, String server, String executor);

    void addIPTempMute(UUID uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addKick(UUID uuid, String user, String ip, String reason, String server, String executor);

    void addWarn(UUID uuid, String user, String ip, String reason, String server, String executor);

}