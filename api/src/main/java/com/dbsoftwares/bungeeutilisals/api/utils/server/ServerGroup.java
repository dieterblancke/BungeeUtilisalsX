package com.dbsoftwares.bungeeutilisals.api.utils.server;

/*
 * Created by DBSoftwares on 24 juni 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

@Data
@AllArgsConstructor
public class ServerGroup {

    private String name;
    private boolean global;
    private List<String> servers;

    public List<ServerInfo> getServerInfos() {
        List<ServerInfo> servers = Lists.newArrayList();

        this.servers.forEach(serverName -> {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);

            if (info != null) {
                servers.add(info);
            }
        });

        return servers;
    }
}
