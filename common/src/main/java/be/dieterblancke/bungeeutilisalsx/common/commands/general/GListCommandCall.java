/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
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

        final long hiddenUsers = this.getHiddenUsers();

        if ( config.exists( "glist.header" ) )
        {
            if ( config.isSection( "glist.header" ) )
            {
                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.header" ),
                                "%total%", BuX.getApi().getPlayerUtils().getTotalCount() - hiddenUsers,
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                        StaffUtils.filterPlayerList( BuX.getApi().getPlayerUtils().getPlayers() )
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
                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                "%server%", info.getName(),
                                "%players%", String.valueOf( info.getPlayers().size() - getHiddenUsers( info ) ),
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join( StaffUtils.filterPlayerList(
                                        info.getPlayers()
                                ) ) )
                        )
                );
            }
        }
        messages.add(
                MessageBuilder.buildMessage(
                        user,
                        config.getSection( "glist.total" ),
                        "%total%", BuX.getApi().getPlayerUtils().getTotalCount() - hiddenUsers,
                        "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                StaffUtils.filterPlayerList( BuX.getApi().getPlayerUtils().getPlayers() )
                        ) )
                )
        );

        messages.forEach( user::sendMessage );
    }

    private long getHiddenUsers( final ServerGroup group )
    {
        return this.getHiddenUsers( group.getPlayerList() );
    }

    private long getHiddenUsers( final IProxyServer serverInfo )
    {
        return this.getHiddenUsers( BuX.getApi().getPlayerUtils().getPlayers( serverInfo.getName() ) );
    }

    private long getHiddenUsers()
    {
        return this.getHiddenUsers( BuX.getApi().getPlayerUtils().getPlayers() );
    }

    private long getHiddenUsers( final List<String> users )
    {
        return users.stream()
                .filter( StaffUtils::isHidden )
                .count();
    }
}
