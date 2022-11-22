package be.dieterblancke.bungeeutilisalsx.bungee.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.UUID;

public class RedisPlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( String server )
    {
        final ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        return info == null ? 0 : RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer( server ).size();
    }

    @Override
    public List<String> getPlayers( String server )
    {
        final List<String> players = Lists.newArrayList();
        final ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        if ( info != null )
        {
            RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer( server ).forEach( uuid ->
                    players.add( RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid( uuid ) ) );
        }

        return players;
    }

    @Override
    public int getTotalCount()
    {
        return RedisBungeeAPI.getRedisBungeeApi().getPlayerCount();
    }

    @Override
    public List<String> getPlayers()
    {
        final List<String> players = Lists.newArrayList();

        RedisBungeeAPI.getRedisBungeeApi().getPlayersOnline().forEach( uuid -> players.add( RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid( uuid ) ) );

        return players;
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        final UUID uuid = RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( name );

        if ( RedisBungeeAPI.getRedisBungeeApi().isPlayerOnline( uuid ) )
        {
            return BuX.getInstance().proxyOperations().getServerInfo( RedisBungeeAPI.getRedisBungeeApi().getServerFor( uuid ) );
        }

        return null;
    }

    @Override
    public boolean isOnline( String name )
    {
        final UUID uuid = RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( name );

        if ( uuid == null )
        {
            return false;
        }
        return RedisBungeeAPI.getRedisBungeeApi().isPlayerOnline( uuid );
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        return RedisBungeeAPI.getRedisBungeeApi().getUuidFromName( targetName, false );
    }
}