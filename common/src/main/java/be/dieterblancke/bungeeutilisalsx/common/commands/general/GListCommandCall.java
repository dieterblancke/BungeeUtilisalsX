package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public class GListCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();
        final String color = config.getString( "glist.playerlist.color" );
        final String separator = config.getString( "glist.playerlist.separator" );
        final List<Component> messages = Lists.newArrayList();
        final List<String> onlinePlayers = BuX.getApi().getPlayerUtils().getPlayers();
        final long hiddenUsers = this.getHiddenUsers( onlinePlayers );
        final long totalOnlineCount = BuX.getApi().getPlayerUtils().getTotalCount() - hiddenUsers;

        if ( config.exists( "glist.header" ) )
        {
            if ( config.isSection( "glist.header" ) )
            {
                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.header" ),
                                MessagePlaceholders.create()
                                        .append( "total", totalOnlineCount )
                                        .append( "playerlist", color + Joiner.on( separator ).join( StaffUtils.filterPlayerList( onlinePlayers ) ) )
                        )
                );
            }
            else
            {
                messages.add( Utils.format( config.getString( "glist.header" ) ) );
            }
        }

        if ( config.getBoolean( "glist.servers.enabled" ) )
        {
            for ( String server : config.getStringList( "glist.servers.list" ) )
            {
                final Optional<ServerGroup> optionalGroup = ConfigFiles.SERVERGROUPS.getServer( server );

                if ( optionalGroup.isEmpty() )
                {
                    BuX.getLogger().warning( "Could not find a servergroup or -name for " + server + "!" );
                    return;
                }
                final ServerGroup group = optionalGroup.get();

                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                MessagePlaceholders.create()
                                        .append( "server", group.getName() )
                                        .append( "players", String.valueOf( group.getPlayers() - getHiddenUsers( group ) ) )
                                        .append( "playerlist", color + Joiner.on( separator ).join( StaffUtils.filterPlayerList( group.getPlayerList() ) ) )
                        )
                );
            }
        }
        else
        {
            for ( IProxyServer info : BuX.getInstance().serverOperations().getServers() )
            {
                final List<String> players = BuX.getApi().getPlayerUtils().getPlayers( info.getName() );

                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                MessagePlaceholders.create()
                                        .append( "server", info.getName() )
                                        .append( "players", String.valueOf( players.size() - getHiddenUsers( players ) ) )
                                        .append( "playerlist", color + Joiner.on( separator ).join( StaffUtils.filterPlayerList( players ) ) )
                        )
                );
            }
        }
        messages.add(
                MessageBuilder.buildMessage(
                        user,
                        config.getSection( "glist.total" ),
                        MessagePlaceholders.create()
                                .append( "total", totalOnlineCount )
                                .append( "playerlist", color + Joiner.on( separator ).join( StaffUtils.filterPlayerList( onlinePlayers ) ) )
                )
        );

        messages.forEach( user::sendMessage );
    }

    @Override
    public String getDescription()
    {
        return "Lists the players online on each server in a very configurable way, with multi proxy support.";
    }

    @Override
    public String getUsage()
    {
        return "/glist";
    }

    private long getHiddenUsers( final ServerGroup group )
    {
        return this.getHiddenUsers( group.getPlayerList() );
    }

    private long getHiddenUsers( final List<String> users )
    {
        return users.stream()
                .filter( StaffUtils::isHidden )
                .count();
    }
}
