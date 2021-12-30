package be.dieterblancke.bungeeutilisalsx.velocity.hubbalancer.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;

import java.util.List;
import java.util.stream.Collectors;

public class HubCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        if ( args.length == 0 )
        {
            return BuX.getApi().getHubBalancer().getServers().stream()
                    .filter( server -> server.isType( HubServerType.LOBBY ) )
                    .map( ServerData::getName )
                    .collect( Collectors.toList() );
        }
        else
        {
            final String lastWord = args[args.length - 1];

            return BuX.getApi().getHubBalancer().getServers().stream()
                    .filter( server -> server.isType( HubServerType.LOBBY ) )
                    .map( ServerData::getName )
                    .filter( server -> server.toLowerCase().startsWith( lastWord.toLowerCase() ) )
                    .collect( Collectors.toList() );
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user instanceof ConsoleUser )
        {
            user.sendMessage( "This command is for players only!" );
            return;
        }

        final String serverName = user.getServerName();
        final List<ServerData> lobbies = BuX.getApi().getHubBalancer().getServers().stream()
                .filter( server -> server.isType( HubServerType.LOBBY ) )
                .collect( Collectors.toList() );

        if ( args.size() == 0 )
        {
            if ( lobbies.stream().anyMatch( server -> server.getName().equalsIgnoreCase( serverName ) ) )
            {
                user.sendLangMessage( "hubbalancer.already-in-lobby" );
                return;
            }

            final ServerData optimal = BuX.getApi().getHubBalancer().findBestServer( HubServerType.LOBBY );

            if ( optimal == null || optimal.getServerInfo() == null )
            {
                user.sendLangMessage( "hubbalancer.no-lobbies-found" );

                return;
            }

            user.sendLangMessage( "hubbalancer.connected", "{serverName}", optimal.getName() );
            user.getStorage().setData( UserStorageKey.HUBBALANCER_NO_REDIRECT, true );
            user.sendToServer( optimal.getServerInfo() );
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

                        user.getStorage().setData( UserStorageKey.HUBBALANCER_NO_REDIRECT, true );
                        user.sendToServer( target.getServerInfo() );
                    }
                    else
                    {
                        user.sendLangMessage( "offline", "{serverName}", target.getName() );
                    }
                }
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Sends you to one of the hub servers.";
    }

    @Override
    public String getUsage()
    {
        return "/hub [hubNumber]";
    }
}
