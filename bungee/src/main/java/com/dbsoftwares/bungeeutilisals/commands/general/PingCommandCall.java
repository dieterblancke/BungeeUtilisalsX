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
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;

import java.util.List;
import java.util.Optional;

public class PingCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.ping.message" );
        }
        else
        {
            final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.permission-other" );
            if ( permission != null
                    && !permission.isEmpty()
                    && !user.hasPermission( permission )
                    && !user.hasPermission( "bungeeutilisals.commands.*" )
                    && !user.hasPermission( "bungeeutilisals.*" )
                    && !user.hasPermission( "*" ) )
            {
                user.sendLangMessage( "no-permission", "%permission%", permission );
                return;
            }

            final Optional<User> optionalUser = BUCore.getApi().getUser( args.get( 0 ) );

            if ( !optionalUser.isPresent() )
            {
                // If using redis, check is player is online in redisbungee
                if ( BUCore.getApi().getBridgeManager().useBridging() && BUCore.getApi().getPlayerUtils().isOnline( args.get( 0 ) ) )
                {
                    BUCore.getApi().getBridgeManager().getBridge().sendMessage(
                            BridgeType.BUNGEE_BUNGEE,
                            "GET_USER_PING",
                            args.get( 0 ),
                            Integer.class,
                            ping -> user.sendLangMessage(
                                    "general-commands.ping.other",
                                    "{target}", args.get( 0 ),
                                    "{targetPing}", ping
                            )
                    );
                }
                else
                {
                    user.sendLangMessage( "offline" );
                }
                return;
            }
            final User target = optionalUser.get();

            user.sendLangMessage(
                    "general-commands.ping.other",
                    "{target}", target.getName(),
                    "{targetPing}", target.getPing()
            );
        }
    }
}
