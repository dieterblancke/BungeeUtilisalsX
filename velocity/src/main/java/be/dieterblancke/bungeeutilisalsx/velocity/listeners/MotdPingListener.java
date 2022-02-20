package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityMotdConnection;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.proxy.server.ServerPing.SamplePlayer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.stream.Collectors;

public class MotdPingListener
{

    @Subscribe
    public void onPing( final ProxyPingEvent event )
    {
        final VelocityMotdConnection motdConnection = this.getConnection( event.getConnection() );
        final ProxyMotdPingEvent proxyMotdPingEvent = new ProxyMotdPingEvent(
                motdConnection,
                ( e ) ->
                {
                    if ( e.getMotdPingResponse() == null )
                    {
                        return;
                    }

                    final ServerPing orig = event.getPing();
                    final ServerPing serverPing = new ServerPing(
                            new ServerPing.Version( orig.getVersion().getProtocol(), orig.getVersion().getName() ),
                            new ServerPing.Players(
                                    orig.getPlayers().map( ServerPing.Players::getOnline ).orElse( 0 ),
                                    orig.getPlayers().map( ServerPing.Players::getMax ).orElse( 0 ),
                                    e.getMotdPingResponse().getPlayers()
                                            .stream()
                                            .map( it -> new SamplePlayer( it.getName(), it.getUuid() ) )
                                            .collect( Collectors.toList() )
                            ),
                            GsonComponentSerializer.gson().deserialize(
                                    ComponentSerializer.toString( e.getMotdPingResponse().getMotd() )
                            ),
                            orig.getFavicon().orElse( null )
                    );

                    event.setPing( serverPing );
                }
        );

        BuX.getApi().getEventLoader().launchEvent( proxyMotdPingEvent );
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private VelocityMotdConnection getConnection( final InboundConnection connection )
    {
        return new VelocityMotdConnection(
                connection.getProtocolVersion().getProtocol(),
                connection.getRemoteAddress(),
                connection.getVirtualHost().orElse( null )
        );
    }
}