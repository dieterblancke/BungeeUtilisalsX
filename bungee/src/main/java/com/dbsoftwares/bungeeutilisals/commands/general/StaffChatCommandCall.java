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
import com.dbsoftwares.bungeeutilisals.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserActionType;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.util.BridgedUserMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StaffChatCommandCall implements CommandCall, Listener
{
    public static void sendStaffChatMessage( String serverName, String userName, String message )
    {
        handleStaffChatMessage( serverName, userName, message );

        if ( BUCore.getApi().getBridgeManager().useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Lists.newArrayList(
                    ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ),
                    "bungeeutilisals.commands.*",
                    "bungeeutilisals.*",
                    "*"
            ) );

            BUCore.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    true,
                                    "general-commands.staffchat.format",
                                    data,
                                    "{user}", userName,
                                    "{server}", serverName,
                                    "{message}", message
                            )
                    )
            );
        }
    }

    private static void handleStaffChatMessage( String serverName, String userName, String message )
    {
        for ( User user : BUCore.getApi().getUsers() )
        {
            ProxiedPlayer parent = user.getParent();

            if ( parent.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ) )
                    || parent.hasPermission( "bungeeutilisals.commands.*" )
                    || parent.hasPermission( "bungeeutilisals.*" )
                    || parent.hasPermission( "*" ) )
            {
                user.sendLangMessage( false, "general-commands.staffchat.format",
                        "{user}", userName, "{server}", serverName, "{message}", message );
            }
        }
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        // If amount of arguments > 0, then we should directly send a message in staff chat
        if ( args.size() > 0 )
        {
            sendStaffChatMessage( user.getServerName(), user.getName(), String.join( " ", args ) );
            return;
        }

        user.setInStaffChat( !user.isInStaffChat() );

        user.sendLangMessage( "general-commands.staffchat."
                + ( user.isInStaffChat() ? "enabled" : "disabled" ) );
    }

    @EventHandler
    public void onChat( ChatEvent event )
    {
        if ( event.isCommand() || event.isCancelled() )
        {
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        BUCore.getApi().getUser( player ).ifPresent( user ->
        {
            if ( user.isInStaffChat() )
            {
                if ( player.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ) )
                        || player.hasPermission( "bungeeutilisals.commands.*" )
                        || player.hasPermission( "bungeeutilisals.*" )
                        || player.hasPermission( "*" ) )
                {
                    event.setCancelled( true );

                    sendStaffChatMessage( user.getServerName(), user.getName(), event.getMessage() );

                    if ( BUCore.getApi().getBridgeManager().useBridging() )
                    {
                        BUCore.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                                BridgeType.BUNGEE_BUNGEE,
                                null,
                                Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                                "USER",
                                new UserAction(
                                        null,
                                        UserActionType.MESSAGE,
                                        new BridgedUserMessage(
                                                true,
                                                "general-commands.staffchat.format",
                                                Maps.newHashMap(),
                                                "{user}", user.getName(),
                                                "{server}", user.getServerName(),
                                                "{message}", event.getMessage()
                                        )
                                )
                        );
                    }
                }
                else
                {
                    user.setInStaffChat( false );
                }
            }
        } );
    }
}
