package com.dbsoftwares.bungeeutilisalsx.common;

import com.dbsoftwares.bungeeutilisalsx.common.api.command.Command;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.dump.PluginInfo;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.IProxyServer;

import java.util.List;

public interface ProxyOperationsApi
{

    void registerCommand( final Command command );

    void unregisterCommand( final Command command );

    List<IProxyServer> getServers();

    IProxyServer getServerInfo( String serverName );

    String getProxyIdentifier();

    List<PluginInfo> getPlugins();

}
