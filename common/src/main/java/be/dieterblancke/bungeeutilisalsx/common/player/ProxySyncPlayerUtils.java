package be.dieterblancke.bungeeutilisalsx.common.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.proxysync.api.ProxySyncApiProvider;
import be.dieterblancke.proxysync.api.model.user.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProxySyncPlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( final String server )
    {
        final IProxyServer proxyServer = BuX.getInstance().proxyOperations().getServerInfo( server );

        // checking if server really exists
        return proxyServer == null ? 0 : ProxySyncApiProvider.getApi().getProxyManager().getProxyUserCount( server );
    }

    @Override
    public List<String> getPlayers( final String server )
    {
        return ProxySyncApiProvider.getApi().getProxyManager().getPlayersOnServer( server )
                .stream()
                .map( User::getUsername )
                .collect( Collectors.toList() );
    }

    @Override
    public int getTotalCount()
    {
        return ProxySyncApiProvider.getApi().getTotalUserCount();
    }

    @Override
    public List<String> getPlayers()
    {
        return ProxySyncApiProvider.getApi().getUserManager().getOnlineUsers()
                .stream()
                .map( User::getUsername )
                .collect( Collectors.toList() );
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        final User user = ProxySyncApiProvider.getApi().getUserManager().getUser( name );

        if ( user.isOnline() )
        {
            return BuX.getInstance().proxyOperations().getServerInfo( user.getServer() );
        }

        return null;
    }

    @Override
    public boolean isOnline( String name )
    {
        final User user = ProxySyncApiProvider.getApi().getUserManager().getUser( name );

        return user.isOnline();
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        return ProxySyncApiProvider.getApi().getUserManager().getUuidFromUsername( targetName );
    }
}
