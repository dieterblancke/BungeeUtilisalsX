package dev.endoy.bungeeutilisalsx.velocity.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing.Players;
import lombok.Data;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
public class VelocityServer implements IProxyServer
{

    private final RegisteredServer registeredServer;

    public VelocityServer( final RegisteredServer registeredServer )
    {
        this.registeredServer = registeredServer;
    }

    @Override
    public String getName()
    {
        return registeredServer.getServerInfo().getName();
    }

    @Override
    public Collection<String> getPlayers()
    {
        return registeredServer.getPlayersConnected().stream().map( Player::getUsername ).collect( Collectors.toList() );
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
        final MinecraftChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.from( channel );

        registeredServer.sendPluginMessage( channelIdentifier, data );
    }

    @Override
    public CompletableFuture<PingInfo> ping()
    {
        return registeredServer.ping().thenApply( serverPing -> new PingInfo(
                serverPing.getPlayers().map( Players::getOnline ).orElse( 0 ),
                serverPing.getPlayers().map( Players::getMax ).orElse( 0 ),
                GsonComponentSerializer.gson().serialize( serverPing.getDescriptionComponent() )
        ) );
    }
}
