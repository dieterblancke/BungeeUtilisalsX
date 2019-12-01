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

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.utils.MessageBuilder;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class GListCommand extends BUCommand
{

    public GListCommand()
    {
        super(
                "glist",
                Arrays.asList( FileLocation.GENERALCOMMANDS.getConfiguration().getString( "glist.aliases" ).split( ", " ) ),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString( "glist.permission" )
        );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return ImmutableList.of();
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        IConfiguration config = FileLocation.GENERALCOMMANDS.getConfiguration();
        String color = config.getString( "glist.playerlist.color" );
        String separator = config.getString( "glist.playerlist.separator" );
        List<TextComponent> messages = Lists.newArrayList();

        if (config.exists( "glist.header" )) {
            messages.add( MessageBuilder.buildMessage( user, config.getSection( "glist.header" ),
                    "%total%", BUCore.getApi().getPlayerUtils().getTotalCount(),
                    "%playerlist%", Utils.c( color + Joiner.on( separator )
                            .join( BUCore.getApi().getPlayerUtils().getPlayers() ) )
            ) );
        }

        if ( config.getBoolean( "glist.servers.enabled" ) )
        {
            for ( String server : config.getStringList( "glist.servers.list" ) )
            {
                ServerGroup group = FileLocation.SERVERGROUPS.getData( server );

                if ( group == null )
                {
                    BUCore.getLogger().warn( "Could not find a servergroup or -name for " + server + "!" );
                    return;
                }

                messages.add( MessageBuilder.buildMessage( user, config.getSection( "glist.format" ),
                        "%server%", group.getName(),
                        "%players%", String.valueOf( group.getPlayers() ),
                        "%playerlist%", Utils.c( color + Joiner.on( separator ).join( group.getPlayerList() ) )
                ) );
            }
        }
        else
        {
            for ( ServerInfo info : ProxyServer.getInstance().getServers().values() )
            {
                messages.add( MessageBuilder.buildMessage( user, config.getSection( "glist.format" ),
                        "%server%", info.getName(),
                        "%players%", String.valueOf( info.getPlayers().size() ),
                        "%playerlist%", Utils.c( color + Joiner.on( separator ).join( info.getPlayers() ) )
                ) );
            }
        }
        messages.add( MessageBuilder.buildMessage( user, config.getSection( "glist.total" ),
                "%total%", BUCore.getApi().getPlayerUtils().getTotalCount(),
                "%playerlist%", Utils.c( color + Joiner.on( separator )
                        .join( BUCore.getApi().getPlayerUtils().getPlayers() ) )
        ) );

        messages.forEach( user::sendMessage );
    }
}
