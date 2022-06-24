package be.dieterblancke.bungeeutilisalsx.bungee.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class BungeePlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( String server )
    {
        ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        return info == null ? 0 : info.getPlayers().size();
    }

    @Override
    public List<String> getPlayers( String server )
    {
        List<String> players = Lists.newArrayList();
        ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        if ( info != null )
        {
            info.getPlayers().forEach( player -> players.add( player.getName() ) );
        }

        return players;
    }

    @Override
    public int getTotalCount()
    {
        return ProxyServer.getInstance().getPlayers().size();
    }

    @Override
    public List<String> getPlayers()
    {
        List<String> players = Lists.newArrayList();

        ProxyServer.getInstance().getPlayers().forEach( player -> players.add( player.getName() ) );

        return players;
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer( name );

        if ( player != null )
        {
            return BuX.getInstance().proxyOperations().getServerInfo( player.getServer().getInfo().getName() );
        }

        return null;
    }

    @Override
    public boolean isOnline( String name )
    {
        return ProxyServer.getInstance().getPlayer( name ) != null;
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( targetName );

        if ( player != null )
        {
            return player.getUniqueId();
        }
        return null;
    }
}
