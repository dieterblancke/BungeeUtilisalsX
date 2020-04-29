package com.dbsoftwares.bungeeutilisals.hubbalancer.commands;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;

import java.util.List;
import java.util.stream.Collectors;

public class HubCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        if ( args.length == 0 )
        {
            return BUCore.getApi().getHubBalancer().getServers().stream()
                    .filter( server -> server.isType( ServerData.ServerType.LOBBY ) )
                    .map( ServerData::getName )
                    .collect( Collectors.toList() );
        }
        else
        {
            final String lastWord = args[args.length - 1];

            return BUCore.getApi().getHubBalancer().getServers().stream()
                    .filter( server -> server.isType( ServerData.ServerType.LOBBY ) )
                    .map( ServerData::getName )
                    .filter( server -> server.toLowerCase().startsWith( lastWord.toLowerCase() ) )
                    .collect( Collectors.toList() );
        }
    }

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        if ( user instanceof ConsoleUser )
        {
            user.sendMessage( "This command is for players only!" );
            return;
        }

        final String serverName = user.getServerName();
        final List<ServerData> lobbies = BUCore.getApi().getHubBalancer().getServers().stream()
                .filter( server -> server.isType( ServerData.ServerType.LOBBY ) )
                .collect( Collectors.toList() );

        if ( args.size() == 0 )
        {
            if ( lobbies.stream().anyMatch( server -> server.getName().equalsIgnoreCase( serverName ) ) )
            {
                user.sendLangMessage( "hubbalancer.already-in-lobby" );
                return;
            }

            final ServerData optimal = BUCore.getApi().getHubBalancer().findBestServer( ServerData.ServerType.LOBBY );

            if ( optimal == null || optimal.getServerInfo() == null )
            {
                user.sendLangMessage( "hubbalancer.no-lobbies-found" );

                return;
            }

            user.sendLangMessage( "hubbalancer.connected", "{serverName}", optimal.getName() );
            user.getStorage().setData( "HUBBALANCER_NO_REDIRECT", true );
            user.getParent().connect( optimal.getServerInfo() );
        }
        else
        {
            final String targetServer = args.get( 0 );
            final ServerData target;

            if ( MathUtils.isInteger( targetServer ) )
            {
                final int id = Integer.parseInt( targetServer ) - 1;

                if ( id >= lobbies.size() )
                {
                    user.sendLangMessage( "hubbalancer.invalid-id", "{lobbyAmount}", lobbies.size() );
                    return;
                }
                else
                {
                    target = lobbies.get( id );
                }
            }
            else
            {
                target = lobbies.stream()
                        .filter( server -> server.getName().toLowerCase().contains( targetServer.toLowerCase() ) )
                        .findFirst()
                        .orElse( null );
            }
            if ( target == null )
            {
                user.sendLangMessage( "hubbalancer.server-not-found", "{serverName}", targetServer );
            }
            else
            {
                if ( serverName.equalsIgnoreCase( target.getName() ) )
                {
                    user.sendLangMessage( "hubbalancer.already-connected", "{serverName}", target.getName() );
                }
                else
                {
                    if ( target.isOnline() )
                    {
                        user.sendLangMessage( "hubbalancer.connected", "{serverName}", target.getName() );

                        user.getStorage().setData( "HUBBALANCER_NO_REDIRECT", true );
                        user.getParent().connect( target.getServerInfo() );
                    }
                    else
                    {
                        user.sendLangMessage( "offline", "{serverName}", target.getName() );
                    }
                }
            }
        }
    }
}
