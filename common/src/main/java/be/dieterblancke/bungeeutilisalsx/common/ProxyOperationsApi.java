package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.dump.PluginInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;

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
