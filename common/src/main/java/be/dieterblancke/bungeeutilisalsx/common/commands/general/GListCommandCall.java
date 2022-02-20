package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

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
        final List<TextComponent> messages = Lists.newArrayList();
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
                                "%total%", totalOnlineCount,
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                        StaffUtils.filterPlayerList( onlinePlayers )
                                ) )
                        )
                );
            }
            else
            {
                messages.add( new TextComponent( Utils.format( config.getString( "glist.header" ) ) ) );
            }
        }

        if ( config.getBoolean( "glist.servers.enabled" ) )
        {
            for ( String server : config.getStringList( "glist.servers.list" ) )
            {
                final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( server );

                if ( group == null )
                {
                    BuX.getLogger().warning( "Could not find a servergroup or -name for " + server + "!" );
                    return;
                }

                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                "%server%", group.getName(),
                                "%players%", String.valueOf( group.getPlayers() - getHiddenUsers( group ) ),
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join( StaffUtils.filterPlayerList( group.getPlayerList() ) ) )
                        )
                );
            }
        }
        else
        {
            for ( IProxyServer info : BuX.getInstance().proxyOperations().getServers() )
            {
                final List<String> players = BuX.getApi().getPlayerUtils().getPlayers( info.getName() );

                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                "%server%", info.getName(),
                                "%players%", String.valueOf( players.size() - getHiddenUsers( players ) ),
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                        StaffUtils.filterPlayerList( players )
                                ) )
                        )
                );
            }
        }
        messages.add(
                MessageBuilder.buildMessage(
                        user,
                        config.getSection( "glist.total" ),
                        "%total%", totalOnlineCount,
                        "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                StaffUtils.filterPlayerList( onlinePlayers )
                        ) )
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
