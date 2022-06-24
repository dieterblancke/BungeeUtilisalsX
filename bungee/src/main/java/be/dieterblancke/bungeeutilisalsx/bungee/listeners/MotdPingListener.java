package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.TritonPluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeMotdConnection;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class MotdPingListener implements Listener
{

    @EventHandler
    public void onPing( final ProxyPingEvent event )
    {
        event.registerIntent( Bootstrap.getInstance() );

        final MotdConnection motdConnection = this.createMotdConnection( event.getConnection() );
        final ProxyMotdPingEvent proxyMotdPingEvent = new ProxyMotdPingEvent(
                motdConnection,
                ( e ) ->
                {
                    if ( e.getMotdPingResponse() == null )
                    {
                        return;
                    }
                    final ServerPing orig = event.getResponse();

                    event.getResponse().setPlayers( new Players(
                            orig.getPlayers().getMax(),
                            orig.getPlayers().getOnline(),
                            e.getMotdPingResponse().getPlayers()
                                    .stream()
                                    .map( it -> new PlayerInfo( it.getName(), it.getUuid() ) )
                                    .toArray( PlayerInfo[]::new )
                    ) );
                    event.getResponse().setDescriptionComponent( new TextComponent( BungeeComponentSerializer.get().serialize( e.getMotdPingResponse().getMotd() ) ) );

                    PluginSupport.getPluginSupport( TritonPluginSupport.class )
                            .ifPresent( tritonPluginSupport -> tritonPluginSupport.handleMotd( event ) );

                    event.completeIntent( Bootstrap.getInstance() );
                }
        );

        BuX.getApi().getEventLoader().launchEventAsync( proxyMotdPingEvent );
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private MotdConnection createMotdConnection( final PendingConnection connection )
    {
        return new BungeeMotdConnection(
                connection.getVersion(),
                (InetSocketAddress) connection.getSocketAddress(),
                connection.getVirtualHost()
        );
    }
}