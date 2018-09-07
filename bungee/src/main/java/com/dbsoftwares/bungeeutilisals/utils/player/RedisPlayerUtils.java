package com.dbsoftwares.bungeeutilisals.utils.player;

import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 22 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class RedisPlayerUtils implements IPlayerUtils {

    @Override
    public int getPlayerCount(String server) {
        final ServerInfo info = ProxyServer.getInstance().getServerInfo(server); // checking if server really exists

        return info == null ? 0 : RedisBungee.getApi().getPlayersOnServer(server).size();
    }

    @Override
    public List<String> getPlayers(String server) {
        final List<String> players = Lists.newArrayList();
        final ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

        if (info != null) {
            RedisBungee.getApi().getPlayersOnServer(server).forEach(uuid ->
                    players.add(RedisBungee.getApi().getNameFromUuid(uuid)));
        }

        return players;
    }

    @Override
    public int getTotalCount() {
        return RedisBungee.getApi().getPlayerCount();
    }

    @Override
    public List<String> getPlayers() {
        final List<String> players = Lists.newArrayList();

        RedisBungee.getApi().getPlayersOnline().forEach(uuid -> players.add(RedisBungee.getApi().getNameFromUuid(uuid)));

        return players;
    }

    @Override
    public ServerInfo findPlayer(String name) {
        final UUID uuid = RedisBungee.getApi().getUuidFromName(name);

        if (RedisBungee.getApi().isPlayerOnline(uuid)) {
            return RedisBungee.getApi().getServerFor(uuid);
        }

        return null;
    }

    @Override
    public boolean isOnline(String name) {
        final UUID uuid = RedisBungee.getApi().getUuidFromName(name);

        return RedisBungee.getApi().isPlayerOnline(uuid);
    }
}