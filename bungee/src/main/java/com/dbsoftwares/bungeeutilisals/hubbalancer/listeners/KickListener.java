package com.dbsoftwares.bungeeutilisals.hubbalancer.listeners;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class KickListener implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin( ServerKickEvent event )
    {
        final ISection section = ConfigFiles.HUBBALANCER.getConfig().getSection( "fallback-trigger" );
        final String reason = BaseComponent.toLegacyText( event.getKickReasonComponent() );
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

        ServerInfo kickedFrom;

        if ( event.getPlayer().getServer() != null )
        {
            kickedFrom = event.getPlayer().getServer().getInfo();
        }
        else if ( ProxyServer.getInstance().getReconnectHandler() != null )
        {
            kickedFrom = ProxyServer.getInstance().getReconnectHandler().getServer( event.getPlayer() );
        }
        else
        {
            kickedFrom = AbstractReconnectHandler.getForcedHost( event.getPlayer().getPendingConnection() );

            if ( kickedFrom == null )
            {
                kickedFrom = ProxyServer.getInstance().getServerInfo(
                        event.getPlayer().getPendingConnection().getListener().getDefaultServer()
                );
            }
        }

        if ( kickedFrom == null )
        {
            kickedFrom = event.getKickedFrom();
        }

        if ( fallback && kickedFrom != null )
        {
            final ServerInfo finalKickedFrom = kickedFrom;
            ServerData data = BUCore.getApi().getHubBalancer().findBestServer(
                    ServerData.ServerType.FALLBACK,
                    BUCore.getApi().getHubBalancer().getServers().stream().filter(
                            server -> !server.getName().equalsIgnoreCase( finalKickedFrom.getName() )
                    )
            );

            if ( data == null || data.getServerInfo() == null )
            {
                final IConfiguration language = BUCore.getApi().getLanguageManager().getLanguageConfiguration(
                        BungeeUtilisals.getInstance().getDescription().getName(),
                        event.getPlayer()
                );

                data = BUCore.getApi().getHubBalancer().findBestServer( ServerData.ServerType.FALLBACK );

                if ( data == null || data.getServerInfo() == null )
                {
                    event.getPlayer().disconnect( Utils.format( String.join( "\n", language.getStringList( "hubbalancer.no-fallback" ) ) ) );
                }
                else
                {
                    if ( event.getPlayer() == null || event.getPlayer().getServer() == null )
                    {
                        return;
                    }
                    if ( !event.getPlayer().getServer().getInfo().equals( data.getServerInfo() ) )
                    {
                        event.getPlayer().connect( data.getServerInfo() );
                    }
                }
                return;
            }

            event.setCancelServer( data.getServerInfo() );
            event.setCancelled( true );
        }
        else
        {
            event.getPlayer().disconnect( event.getKickReasonComponent() );
        }
    }
}
