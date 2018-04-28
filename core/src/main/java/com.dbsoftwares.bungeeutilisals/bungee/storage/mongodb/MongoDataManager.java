package com.dbsoftwares.bungeeutilisals.bungee.storage.mongodb;

/*
 * Created by DBSoftwares on 18/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.DataManager;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MongoDataManager implements DataManager {

    /* UTILITY METHODS */
    private static Language getLanguageOrDefault(String language) {
        return BUCore.getApi().getLanguageManager().getLanguage(language).orElse(BUCore.getApi().getLanguageManager().getDefaultLanguage());
    }

    private static String format(String line, Object... replacements) {
        return String.format(PlaceHolderAPI.formatMessage(line), replacements);
    }

    public MongoDatabase getDatabase() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }

    @Override
    public void insertIntoUsers(String uuid, String username, String ip, String language) {
        Mapping<String, Object> mapping = new Mapping<>(true);
        mapping.append("uuid", uuid).append("username", username).append("ip", ip).append("language", language);

        getDatabase().getCollection(format("{users-table}")).insertOne(new Document(mapping.getMap()));
    }

    @Override
    public PunishmentInfo insertIntoBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.BAN, uuid, user, ip, null, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoIPBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.IPBAN, uuid, user, ip, null, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.TEMPBAN, uuid, user, ip, time, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoIPTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.IPTEMPBAN, uuid, user, ip, time, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.MUTE, uuid, user, ip, null, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoIPMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.IPMUTE, uuid, user, ip, null, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.TEMPMUTE, uuid, user, ip, time, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoIPTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        return insertPunishment(PunishmentType.IPTEMPMUTE, uuid, user, ip, time, reason, server, active, executedby);
    }

    @Override
    public PunishmentInfo insertIntoWarns(String uuid, String user, String ip, String reason, String server, String executedby) {
        return insertPunishment(PunishmentType.WARN, uuid, user, ip, null, reason, server, null, executedby);
    }

    @Override
    public PunishmentInfo insertIntoKicks(String uuid, String user, String ip, String reason, String server, String executedby) {
        return insertPunishment(PunishmentType.KICK, uuid, user, ip, null, reason, server, null, executedby);
    }

    private PunishmentInfo insertPunishment(PunishmentType type, String uuid, String user, String ip, Long time,
                                            String reason, String server, Boolean active, String executedby) {
        PunishmentInfo.PunishmentInfoBuilder builder = PunishmentInfo.builder();

        builder.uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .executedBy(executedby).date(new Date(System.currentTimeMillis())).type(type);

        Mapping<String, Object> mapping = new Mapping<>(true);
        mapping.append("uuid", uuid).append("user", user).append("ip", ip);
        if (time != null) {
            builder.expireTime(time);
            mapping.append("time", time);
        }
        mapping.append("reason", reason).append("server", server).append("date", new Date(System.currentTimeMillis()));
        if (active != null) {
            builder.active(active);
            mapping.append("active", active);
        }
        mapping.append("executed_by", executedby);

        getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}")).insertOne(new Document(mapping.getMap()));

        return builder.build();
    }

    @Override
    public void updateUser(String uuid, String name, String ip, String language) {
        List<Bson> updates = Lists.newArrayList();

        if (name != null) {
            updates.add(Updates.set("name", name));
        }
        if (ip != null) {
            updates.add(Updates.set("ip", ip));
        }
        if (language != null) {
            updates.add(Updates.set("language", language));
        }

        getDatabase().getCollection(format("{users-table}")).findOneAndUpdate(Filters.eq("uuid", uuid), Updates.combine(updates));
    }

    @Override
    public boolean isUserPresent(String name) {
        return getDatabase().getCollection(format("{users-table}")).find(Filters.eq("name", name)).iterator().hasNext();
    }

    @Override
    public boolean isUserPresent(UUID uuid) {
        return getDatabase().getCollection(format("{users-table}")).find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
    }

    private boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive) {
        List<Bson> filters = Lists.newArrayList();

        Validate.ifNotNull(uuid, u -> filters.add(Filters.eq("uuid", u)));
        Validate.ifNotNull(IP, ip -> filters.add(Filters.eq("ip", ip)));
        Validate.ifTrue(checkActive, active -> filters.add(Filters.eq("active", true)));

        if (filters.size() > 1) {
            return getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}"))
                    .find(Filters.and(filters)).iterator().hasNext();
        } else {
            return filters.size() == 1 &&
                    getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}"))
                            .find(filters.get(0)).iterator().hasNext();
        }
    }

    @Override
    public boolean isBanPresent(UUID uuid, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.BAN, uuid, null, checkActive);
    }

    @Override
    public boolean isIPBanPresent(String ip, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.IPBAN, null, ip, checkActive);
    }

    @Override
    public boolean isTempBanPresent(UUID uuid, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.TEMPBAN, uuid, null, checkActive);
    }

    @Override
    public boolean isIPTempBanPresent(String ip, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.IPTEMPBAN, null, ip, checkActive);
    }

    @Override
    public boolean isMutePresent(UUID uuid, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.MUTE, uuid, null, checkActive);
    }

    @Override
    public boolean isIPMutePresent(String ip, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.IPMUTE, null, ip, checkActive);
    }

    @Override
    public boolean isTempMutePresent(UUID uuid, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.TEMPMUTE, uuid, null, checkActive);
    }

    @Override
    public boolean isIPTempMutePresent(String ip, boolean checkActive) {
        return isPunishmentPresent(PunishmentType.IPTEMPMUTE, null, ip, checkActive);
    }

    @Override
    public UserStorage getUser(UUID uuid) {
        UserStorage storage = new UserStorage();

        Document document = getDatabase().getCollection(format("{users-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            storage.setUuid(uuid);
            storage.setUserName(document.getString("username"));
            storage.setIp(document.getString("ip"));
            storage.setLanguage(getLanguageOrDefault(document.getString("language")));
        }

        return storage;
    }

    @Override
    public UserStorage getUser(String name) {
        UserStorage storage = new UserStorage();

        Document document = getDatabase().getCollection(format("{users-table}")).find(Filters.eq("name", name)).first();

        if (document != null) {
            storage.setUuid(UUID.fromString(document.getString("uuid")));
            storage.setUserName(name);
            storage.setIp(document.getString("ip"));
            storage.setLanguage(getLanguageOrDefault(document.getString("language")));
        }

        return storage;
    }

    private PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP) {
        MongoCollection<Document> collection = getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}"));
        Document document = null;

        if (uuid != null) {
            document = collection.find(Filters.eq("uuid", uuid.toString())).first();
        } else if (IP != null) {
            document = collection.find(Filters.eq("ip", IP)).first();
        }

        if (document != null) {
            PunishmentInfo.PunishmentInfoBuilder builder = PunishmentInfo.builder();
            builder.uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by"))
                    .type(type);

            if (document.containsKey("active")) {
                builder.active(document.getBoolean("active"));
            }
            if (document.containsKey("time")) {
                builder.expireTime(document.getLong("time"));
            }

            return builder.build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getBan(UUID uuid) {
        return getPunishment(PunishmentType.BAN, uuid, null);
    }

    @Override
    public PunishmentInfo getIPBan(String IP) {
        return getPunishment(PunishmentType.IPBAN, null, IP);
    }

    @Override
    public PunishmentInfo getTempBan(UUID uuid) {
        return getPunishment(PunishmentType.TEMPBAN, uuid, null);
    }

    @Override
    public PunishmentInfo getIPTempBan(String IP) {
        return getPunishment(PunishmentType.IPTEMPBAN, null, IP);
    }

    @Override
    public PunishmentInfo getMute(UUID uuid) {
        return getPunishment(PunishmentType.MUTE, uuid, null);
    }

    @Override
    public PunishmentInfo getIPMute(String IP) {
        return getPunishment(PunishmentType.IPMUTE, null, IP);
    }

    @Override
    public PunishmentInfo getTempMute(UUID uuid) {
        return getPunishment(PunishmentType.TEMPMUTE, uuid, null);
    }

    @Override
    public PunishmentInfo getIPTempMute(String IP) {
        return getPunishment(PunishmentType.IPTEMPMUTE, null, IP);
    }

    @Override
    public void removeBan(UUID uuid) {
        removePunishment(PunishmentType.BAN, uuid, null);
    }

    @Override
    public void removeIPBan(String IP) {
        removePunishment(PunishmentType.IPBAN, null, IP);
    }

    @Override
    public void removeTempBan(UUID uuid) {
        removePunishment(PunishmentType.TEMPBAN, uuid, null);
    }

    @Override
    public void removeIPTempBan(String IP) {
        removePunishment(PunishmentType.IPTEMPBAN, null, IP);
    }

    @Override
    public void removeMute(UUID uuid) {
        removePunishment(PunishmentType.MUTE, uuid, null);
    }

    @Override
    public void removeIPMute(String IP) {
        removePunishment(PunishmentType.IPMUTE, null, IP);
    }

    @Override
    public void removeTempMute(UUID uuid) {
        removePunishment(PunishmentType.TEMPMUTE, uuid, null);
    }

    @Override
    public void removeIPTempMute(String IP) {
        removePunishment(PunishmentType.IPTEMPMUTE, null, IP);
    }

    private void removePunishment(PunishmentType type, UUID uuid, String ip) {
        if (uuid != null) {
            getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}"))
                    .updateOne(Filters.eq("uuid", uuid), Updates.set("active", false));
        } else if (ip != null) {
            getDatabase().getCollection(format("{" + type.toString().toLowerCase() + "s-table}"))
                    .updateOne(Filters.eq("ip", ip), Updates.set("active", false));
        }
    }
}