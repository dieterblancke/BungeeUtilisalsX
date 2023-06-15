package be.dieterblancke.bungeeutilisalsx.bungee.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
public class BungeeServer implements IProxyServer
{

    private final ServerInfo serverInfo;

    public BungeeServer( final ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }

    @Override
    public String getName()
    {
        return serverInfo.getName();
    }

    @Override
    public Collection<String> getPlayers()
    {
        return serverInfo.getPlayers().stream().map( ProxiedPlayer::getName ).collect( Collectors.toList() );
    }

    @Override
    public Collection<User> getUsers()
    {
        final Collection<String> players = this.getPlayers();

        return BuX.getApi().getUsers()
                .stream()
                .filter( u -> players.contains( u.getName() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public void sendPluginMessage( final String channel, final byte[] data )
    {
        serverInfo.sendData( channel, data );
    }

    @Override
    public CompletableFuture<PingInfo> ping()
    {
        CompletableFuture<PingInfo> completableFuture = new CompletableFuture<>();

        serverInfo.ping( ( serverPing, throwable ) ->
        {
            if ( throwable != null )
            {
                completableFuture.completeExceptionally( throwable );
            }
            else
            {
                completableFuture.complete( new PingInfo(
                        serverPing.getPlayers().getOnline(),
                        serverPing.getPlayers().getMax(),
                        ComponentSerializer.toString( serverPing.getDescriptionComponent() )
                ) );
            }
        } );

        return completableFuture;
    }
}
