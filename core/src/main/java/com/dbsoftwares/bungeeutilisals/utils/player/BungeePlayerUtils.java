package com.dbsoftwares.bungeeutilisals.utils.player;

import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

/*
 * Created by DBSoftwares on 22 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class BungeePlayerUtils implements IPlayerUtils {

    @Override
    public int getPlayerCount(String server) {
        ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

        return info == null ? 0 : info.getPlayers().size();
    }

    @Override
    public List<String> getPlayers(String server) {
        List<String> players = Lists.newArrayList();
        ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

        if (info != null) {
            info.getPlayers().forEach(player -> players.add(player.getName()));
        }

        return players;
    }

    @Override
    public int getTotalCount() {
        return ProxyServer.getInstance().getPlayers().size();
    }

    @Override
    public List<String> getPlayers() {
        List<String> players = Lists.newArrayList();

        ProxyServer.getInstance().getPlayers().forEach(player -> players.add(player.getName()));

        return players;
    }

    @Override
    public ServerInfo findPlayer(String name) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        if (player != null) {
            return player.getServer().getInfo();
        }

        return null;
    }
}
