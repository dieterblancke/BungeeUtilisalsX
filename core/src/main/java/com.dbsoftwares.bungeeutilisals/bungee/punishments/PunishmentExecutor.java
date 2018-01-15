package com.dbsoftwares.bungeeutilisals.bungee.punishments;

import com.dbsoftwares.bungeeutilisals.api.mysql.MySQL;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.tables.*;

public class PunishmentExecutor implements IPunishmentExecutor {

    private boolean find(Class<?> table, String uuid, String name, String ip) {
        StringBuilder builder = new StringBuilder();
        boolean firstArgument = true;

        if (uuid != null) {
            builder.append("uuid = '").append(uuid).append("'");
            firstArgument = false;
        }
        if (name != null) {
            if (!firstArgument) {
                builder.append("AND ");
            }
            builder.append("user = '").append(name).append("'");
            firstArgument = false;
        }
        if (ip != null) {
            if (!firstArgument) {
                builder.append("AND ");
            }
            builder.append("ip = '").append(ip).append("'");
        }

        return MySQL.search(table).select("id").where(builder.toString()).search().isPresent();
    }

    @Override
    public boolean isBanned(String uuid, String name) {
        return find(BansTable.class, uuid, name, null);
    }

    @Override
    public boolean isTempBanned(String uuid, String name) {
        return find(TempBansTable.class, uuid, name, null);
    }

    @Override
    public boolean isIPBanned(String uuid, String name, String IP) {
        return find(IPBansTable.class, uuid, name, IP);
    }

    @Override
    public boolean isIPTempBanned(String uuid, String name, String IP) {
        return find(IPTempBansTable.class, uuid, name, IP);
    }

    @Override
    public boolean isMuted(String uuid, String name) {
        return find(MutesTable.class, uuid, name, null);
    }

    @Override
    public boolean isTempMuted(String uuid, String name) {
        return find(TempMutesTable.class, uuid, name, null);
    }

    @Override
    public boolean isIPMuted(String uuid, String name, String IP) {
        return find(IPMutesTable.class, uuid, name, IP);
    }

    @Override
    public boolean isIPTempMuted(String uuid, String name, String IP) {
        return find(IPTempMutesTable.class, uuid, name, IP);
    }

    @Override
    public void addBan(String uuid, String name, String IP, String reason, String server, String executor) {
        MySQL.insert(new BansTable(uuid, name, IP, reason, server, true, executor));
    }

    @Override
    public void addTempBan(String uuid, String user, String ip, long removeTime, String reason, String server, String executor) {
        MySQL.insert(new TempBansTable(uuid, user, ip, removeTime, reason, server, true, executor));
    }

    @Override
    public void addIPBan(String uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addIPTempBan(String uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addMute(String uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addTempMute(String uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addIPMute(String uuid, String name, String IP, String reason, String server, String executor) {

    }

    @Override
    public void addIPTempMute(String uuid, String user, String ip, long removeTime, String reason, String server, String executor) {

    }

    @Override
    public void addKick(String uuid, String user, String ip, String reason, String server, String executor) {

    }

    @Override
    public void addWarn(String uuid, String user, String ip, String reason, String server, String executor) {

    }
}