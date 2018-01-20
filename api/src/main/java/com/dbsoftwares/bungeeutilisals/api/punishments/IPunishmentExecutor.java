package com.dbsoftwares.bungeeutilisals.api.punishments;

public interface IPunishmentExecutor {

    boolean isBanned(String uuid, String name);

    boolean isTempBanned(String uuid, String name);

    boolean isIPBanned(String uuid, String name, String IP);

    boolean isIPTempBanned(String uuid, String name, String IP);

    boolean isMuted(String uuid, String name);

    boolean isTempMuted(String uuid, String name);

    boolean isIPMuted(String uuid, String name, String IP);

    boolean isIPTempMuted(String uuid, String name, String IP);

    void addBan(String uuid, String name, String IP, String reason, String server, String executor);

    void addTempBan(String uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addIPBan(String uuid, String name, String IP, String reason, String server, String executor);

    void addIPTempBan(String uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addMute(String uuid, String name, String IP, String reason, String server, String executor);

    void addTempMute(String uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addIPMute(String uuid, String name, String IP, String reason, String server, String executor);

    void addIPTempMute(String uuid, String user, String ip, long removeTime, String reason, String server, String executor);

    void addKick(String uuid, String user, String ip, String reason, String server, String executor);

    void addWarn(String uuid, String user, String ip, String reason, String server, String executor);

}