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
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
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
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{bans-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.BAN).build();
    }

    @Override
    public PunishmentInfo insertIntoIPBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{ipbans-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.IPBAN).build();
    }

    @Override
    public PunishmentInfo insertIntoTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("time", time).append("reason", reason)
                .append("server", server).append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{tempbans-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).expireTime(time).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.TEMPBAN).build();
    }

    @Override
    public PunishmentInfo insertIntoIPTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("time", time).append("reason", reason)
                .append("server", server).append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{iptempbans-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).expireTime(time).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.IPTEMPBAN).build();
    }

    @Override
    public PunishmentInfo insertIntoMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{mutes-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.MUTE).build();
    }

    @Override
    public PunishmentInfo insertIntoIPMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{ipmutes-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.IPMUTE).build();
    }

    @Override
    public PunishmentInfo insertIntoTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("time", time).append("reason", reason)
                .append("server", server).append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{tempmutes-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).expireTime(time).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.TEMPMUTE).build();
    }

    @Override
    public PunishmentInfo insertIntoIPTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("time", time).append("reason", reason)
                .append("server", server).append("date", Utils.getCurrentDate()).append("active", active).append("executed_by", executedby);

        getDatabase().getCollection(format("{iptempmutes-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).expireTime(time).reason(reason).server(server)
                .active(active).executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.IPTEMPMUTE).build();
    }

    @Override
    public PunishmentInfo insertIntoWarns(String uuid, String user, String ip, String reason, String server, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("executed_by", executedby);

        getDatabase().getCollection(format("{warns-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.WARN).build();
    }

    @Override
    public PunishmentInfo insertIntoKicks(String uuid, String user, String ip, String reason, String server, String executedby) {
        Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("uuid", uuid).append("user", user).append("ip", ip).append("reason", reason).append("server", server)
                .append("date", Utils.getCurrentDate()).append("executed_by", executedby);

        getDatabase().getCollection(format("{kicks-table}")).insertOne(new Document(mapping.getMap()));

        return PunishmentInfo.builder().uuid(UUID.fromString(uuid)).user(user).IP(ip).reason(reason).server(server)
                .executedBy(executedby).date(new Date(System.currentTimeMillis())).type(PunishmentType.KICK).build();
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

    @Override
    public boolean isBanPresent(UUID uuid, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{bans-table}")).find(
                    Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{bans-table}")).find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
        }
    }

    @Override
    public boolean isIPBanPresent(String ip, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{ipbans-table}")).find(
                    Filters.and(Filters.eq("ip", ip), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{ipbans-table}")).find(Filters.eq("ip", ip)).iterator().hasNext();
        }
    }

    @Override
    public boolean isTempBanPresent(UUID uuid, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{tempbans-table}")).find(
                    Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{tempbans-table}")).find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
        }
    }

    @Override
    public boolean isIPTempBanPresent(String ip, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{iptempbans-table}")).find(
                    Filters.and(Filters.eq("ip", ip), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{iptempbans-table}")).find(Filters.eq("ip", ip)).iterator().hasNext();
        }
    }

    @Override
    public boolean isMutePresent(UUID uuid, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{mutes-table}")).find(
                    Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{mutes-table}")).find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
        }
    }

    @Override
    public boolean isIPMutePresent(String ip, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{ipmutes-table}")).find(
                    Filters.and(Filters.eq("ip", ip), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{ipmutes-table}")).find(Filters.eq("ip", ip)).iterator().hasNext();
        }
    }

    @Override
    public boolean isTempMutePresent(UUID uuid, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{tempmutes-table}")).find(
                    Filters.and(Filters.eq("uuid", uuid.toString()), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{tempmutes-table}")).find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
        }
    }

    @Override
    public boolean isIPTempMutePresent(String ip, boolean checkActive) {
        if (checkActive) {
            return getDatabase().getCollection(format("{iptempmutes-table}")).find(
                    Filters.and(Filters.eq("ip", ip), Filters.eq("active", true))).iterator().hasNext();
        } else {
            return getDatabase().getCollection(format("{iptempmutes-table}")).find(Filters.eq("ip", ip)).iterator().hasNext();
        }
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

    @Override
    public PunishmentInfo getBan(UUID uuid) {
        Document document = getDatabase().getCollection(format("{bans-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server")).date(document.getDate("date"))
                    .active(document.getBoolean("active")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getIPBan(String IP) {
        Document document = getDatabase().getCollection(format("{ipbans-table}")).find(Filters.eq("ip", IP)).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(UUID.fromString(document.getString("uuid"))).user(document.getString("user")).IP(IP)
                    .reason(document.getString("reason")).server(document.getString("server")).date(document.getDate("date"))
                    .active(document.getBoolean("active")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getTempBan(UUID uuid) {
        Document document = getDatabase().getCollection(format("{tempbans-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).active(document.getBoolean("active"))
                    .executedBy(document.getString("executed_by")).expireTime(document.getLong("time"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getIPTempBan(String IP) {
        Document document = getDatabase().getCollection(format("{iptempbans-table}")).find(Filters.eq("ip", IP)).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(UUID.fromString(document.getString("uuid"))).user(document.getString("user")).IP(IP)
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).active(document.getBoolean("active"))
                    .executedBy(document.getString("executed_by")).expireTime(document.getLong("time"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getMute(UUID uuid) {
        Document document = getDatabase().getCollection(format("{mutes-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server")).date(document.getDate("date"))
                    .active(document.getBoolean("active")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getIPMute(String IP) {
        Document document = getDatabase().getCollection(format("{ipmutes-table}")).find(Filters.eq("ip", IP)).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(UUID.fromString(document.getString("uuid"))).user(document.getString("user")).IP(IP)
                    .reason(document.getString("reason")).server(document.getString("server")).date(document.getDate("date"))
                    .active(document.getBoolean("active")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getTempMute(UUID uuid) {
        Document document = getDatabase().getCollection(format("{tempmutes-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).active(document.getBoolean("active"))
                    .executedBy(document.getString("executed_by")).expireTime(document.getLong("time"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public PunishmentInfo getIPTempMute(String IP) {
        Document document = getDatabase().getCollection(format("{iptempmutes-table}")).find(Filters.eq("ip", IP)).first();

        if (document != null) {
            return PunishmentInfo.builder().uuid(UUID.fromString(document.getString("uuid"))).user(document.getString("user")).IP(IP)
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).active(document.getBoolean("active"))
                    .executedBy(document.getString("executed_by")).expireTime(document.getLong("time"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by")).build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public void removeBan(UUID uuid) {
        getDatabase().getCollection(format("{bans-table}")).deleteOne(Filters.eq("uuid", uuid.toString()));
    }

    @Override
    public void removeIPBan(String IP) {
        getDatabase().getCollection(format("{ipbans-table}")).deleteOne(Filters.eq("ip", IP));
    }

    @Override
    public void removeTempBan(UUID uuid) {
        getDatabase().getCollection(format("{tempbans-table}")).deleteOne(Filters.eq("uuid", uuid.toString()));
    }

    @Override
    public void removeIPTempBan(String IP) {
        getDatabase().getCollection(format("{iptempbans-table}")).deleteOne(Filters.eq("ip", IP));
    }

    @Override
    public void removeMute(UUID uuid) {
        getDatabase().getCollection(format("{mutes-table}")).deleteOne(Filters.eq("uuid", uuid.toString()));
    }

    @Override
    public void removeIPMute(String IP) {
        getDatabase().getCollection(format("{ipmutes-table}")).deleteOne(Filters.eq("ip", IP));
    }

    @Override
    public void removeTempMute(UUID uuid) {
        getDatabase().getCollection(format("{tempmutes-table}")).deleteOne(Filters.eq("uuid", uuid.toString()));
    }

    @Override
    public void removeIPTempMute(String IP) {
        getDatabase().getCollection(format("{iptempmutes-table}")).deleteOne(Filters.eq("ip", IP));
    }
}