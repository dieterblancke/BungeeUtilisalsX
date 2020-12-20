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
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserAction;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserActionType;
import be.dieterblancke.bungeeutilisalsx.common.bridge.util.BridgedUserMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class StaffChatCommandCall implements CommandCall
{

    public static void sendStaffChatMessage( final String serverName, final String userName, final String message )
    {
        handleStaffChatMessage( serverName, userName, message );

        if ( BuX.getApi().getBridgeManager().useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Lists.newArrayList(
                    ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ),
                    "bungeeutilisals.commands.*",
                    "bungeeutilisals.*",
                    "*"
            ) );

            BuX.getApi().getBridgeManager().getBridge().sendTargetedMessage(
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
        for ( User user : BuX.getApi().getUsers() )
        {
            if ( user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ) )
                    || user.hasPermission( "bungeeutilisals.commands.*" )
                    || user.hasPermission( "bungeeutilisals.*" )
                    || user.hasPermission( "*" ) )
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
}
