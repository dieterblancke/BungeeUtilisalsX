package com.dbsoftwares.bungeeutilisalsx.velocity.hubbalancer.listeners;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

public class KickListener
{

    @Subscribe( order = PostOrder.LAST )
    public void onLogin( final KickedFromServerEvent event )
    {
        final ISection section = ConfigFiles.HUBBALANCER.getConfig().getSection( "fallback-trigger" );
        final String reason = event.getServerKickReason()
                .map( c -> PlainComponentSerializer.plain().serialize( c ) )
                .orElse( "" );
        boolean fallback;

        if ( section.getString( "type" ).equalsIgnoreCase( "BLACKLIST" ) )
        {
            fallback = true;
            for ( String contain : section.getStringList( "reasons" ) )
            {
                if ( reason.contains( contain ) )
                {
                    fallback = false;
                    break;
                }
            }
        }
        else
        {
            fallback = false;
            for ( String contain : section.getStringList( "reasons" ) )
            {
                if ( reason.contains( contain ) )
                {
                    fallback = true;
                    break;
                }
            }
        }

        final RegisteredServer kickedFrom = event.getServer();

        if ( fallback && kickedFrom != null )
        {
            ServerData data = BuX.getApi().getHubBalancer().findBestServer(
                    HubServerType.FALLBACK,
                    BuX.getApi().getHubBalancer().getServers().stream().filter(
                            server -> !server.getName().equalsIgnoreCase( kickedFrom.getServerInfo().getName() )
                    )
            );

            if ( data == null || data.getServerInfo() == null )
            {
                final IConfiguration language = BuX.getApi().getLanguageManager().getLanguageConfiguration(
                        BuX.getInstance().getName(),
                        event.getPlayer().getUsername()
                );

                data = BuX.getApi().getHubBalancer().findBestServer( HubServerType.FALLBACK );

                if ( data == null || data.getServerInfo() == null )
                {
                    event.getPlayer().disconnect( Component.text(
                            Utils.c( String.join( "\n", language.getStringList( "hubbalancer.no-fallback" ) ) )
                    ) );
                }
                else
                {
                    if ( event.getPlayer() == null || !event.getPlayer().getCurrentServer().isPresent() )
                    {
                        return;
                    }
                    if ( !event.getPlayer().getCurrentServer().get().getServerInfo().getName().equals( data.getServerInfo().getName() ) )
                    {
                        event.getPlayer()
                                .createConnectionRequest( ( (VelocityServer) data.getServerInfo() ).getRegisteredServer() )
                                .fireAndForget();
                    }
                }
                return;
            }

            event.setResult( KickedFromServerEvent.RedirectPlayer.create(
                    ( (VelocityServer) data.getServerInfo() ).getRegisteredServer()
            ) );
        }
        else
        {
            event.setResult( KickedFromServerEvent.DisconnectPlayer.create(
                    event.getServerKickReason().orElse( Component.text( "" ) )
            ) );
        }
    }
}
