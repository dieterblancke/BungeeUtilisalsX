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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.StaffUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        final int hiddenUsers = getHiddenUsers();

        if ( config.exists( "glist.header" ) )
        {
            if ( config.isSection( "glist.header" ) )
            {
                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.header" ),
                                "%total%", BUCore.getApi().getPlayerUtils().getTotalCount() - hiddenUsers,
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                        StaffUtils.filterPlayerList( BUCore.getApi().getPlayerUtils().getPlayers() )
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
                    BUCore.getLogger().warning( "Could not find a servergroup or -name for " + server + "!" );
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
            for ( ServerInfo info : ProxyServer.getInstance().getServers().values() )
            {
                messages.add(
                        MessageBuilder.buildMessage(
                                user,
                                config.getSection( "glist.format" ),
                                "%server%", info.getName(),
                                "%players%", String.valueOf( info.getPlayers().size() - getHiddenUsers( info ) ),
                                "%playerlist%", Utils.c( color + Joiner.on( separator ).join( StaffUtils.filterPlayerList(
                                        info.getPlayers().stream().map( ProxiedPlayer::getName ).collect( Collectors.toList() )
                                ) ) )
                        )
                );
            }
        }
        messages.add(
                MessageBuilder.buildMessage(
                        user,
                        config.getSection( "glist.total" ),
                        "%total%", BUCore.getApi().getPlayerUtils().getTotalCount() - hiddenUsers,
                        "%playerlist%", Utils.c( color + Joiner.on( separator ).join(
                                StaffUtils.filterPlayerList( BUCore.getApi().getPlayerUtils().getPlayers() )
                        ) )
                )
        );

        messages.forEach( user::sendMessage );
    }

    private int getHiddenUsers( final ServerGroup group )
    {
        int hidden = 0;
        for ( StaffUser user : BungeeUtilisals.getInstance().getStaffMembers() )
        {
            if ( user.isHidden() )
            {
                final Optional<User> optUser = BUCore.getApi().getUser( user.getUuid() );
                if ( optUser.isPresent() )
                {
                    if ( group.isInGroup( optUser.get().getServerName() ) )
                    {
                        hidden++;
                    }
                }
                else if ( BUCore.getApi().getBridgeManager().useBridging() )
                {
                    final ServerInfo server = BUCore.getApi().getPlayerUtils().findPlayer( user.getName() );

                    if ( server != null && group.isInGroup( server.getName() ) )
                    {
                        hidden++;
                    }
                }
            }
        }
        return hidden;
    }

    private int getHiddenUsers( final ServerInfo serverInfo )
    {
        int hidden = 0;
        for ( StaffUser user : BungeeUtilisals.getInstance().getStaffMembers() )
        {
            if ( user.isHidden() )
            {
                final Optional<User> optUser = BUCore.getApi().getUser( user.getUuid() );
                if ( optUser.isPresent() )
                {
                    if ( serverInfo.getName().equalsIgnoreCase( optUser.get().getServerName() ) )
                    {
                        hidden++;
                    }
                }
                else if ( BUCore.getApi().getBridgeManager().useBridging() )
                {
                    final ServerInfo server = BUCore.getApi().getPlayerUtils().findPlayer( user.getName() );

                    if ( server != null && serverInfo.getName().equalsIgnoreCase( server.getName() ) )
                    {
                        hidden++;
                    }
                }
            }
        }
        return hidden;
    }

    private int getHiddenUsers()
    {
        int hidden = 0;
        for ( StaffUser user : BungeeUtilisals.getInstance().getStaffMembers() )
        {
            if ( user.isHidden() )
            {
                hidden++;
            }
        }
        return hidden;
    }
}
