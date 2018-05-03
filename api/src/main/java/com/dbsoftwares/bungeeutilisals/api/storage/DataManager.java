package com.dbsoftwares.bungeeutilisals.api.storage;

/*
 * Created by DBSoftwares on 08/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;

import java.util.UUID;

public interface DataManager {

    void insertIntoUsers(String uuid, String username, String ip, String language);

    PunishmentInfo insertIntoBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoIPBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoIPTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoIPMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoIPTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby);

    PunishmentInfo insertIntoWarns(String uuid, String user, String ip, String reason, String server, String executedby);

    PunishmentInfo insertIntoKicks(String uuid, String user, String ip, String reason, String server, String executedby);

    void updateUser(String uuid, String name, String ip, String language);

    boolean isUserPresent(String name);

    boolean isUserPresent(UUID uuid);

    boolean isBanPresent(UUID uuid, boolean checkActive);

    boolean isIPBanPresent(String ip, boolean checkActive);

    boolean isTempBanPresent(UUID uuid, boolean checkActive);

    boolean isIPTempBanPresent(String IP, boolean checkActive);

    boolean isMutePresent(UUID uuid, boolean checkActive);

    boolean isIPMutePresent(String ip, boolean checkActive);

    boolean isTempMutePresent(UUID uuid, boolean checkActive);

    boolean isIPTempMutePresent(String IP, boolean checkActive);

    UserStorage getUser(UUID uuid);

    UserStorage getUser(String name);

    PunishmentInfo getBan(UUID uuid);

    PunishmentInfo getIPBan(String IP);

    PunishmentInfo getTempBan(UUID uuid);

    PunishmentInfo getIPTempBan(String IP);

    PunishmentInfo getMute(UUID uuid);

    PunishmentInfo getIPMute(String IP);

    PunishmentInfo getTempMute(UUID uuid);

    PunishmentInfo getIPTempMute(String IP);

    void removeBan(UUID uuid);

    void removeIPBan(String IP);

    void removeTempBan(UUID uuid);

    void removeIPTempBan(String IP);

    void removeMute(UUID uuid);

    void removeIPMute(String IP);

    void removeTempMute(UUID uuid);

    void removeIPTempMute(String IP);

    Language getLanguage(UUID uuid);
}