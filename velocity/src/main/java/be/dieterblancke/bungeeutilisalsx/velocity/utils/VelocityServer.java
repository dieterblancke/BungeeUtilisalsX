package be.dieterblancke.bungeeutilisalsx.velocity.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Data;

import java.util.Collection;
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
}
