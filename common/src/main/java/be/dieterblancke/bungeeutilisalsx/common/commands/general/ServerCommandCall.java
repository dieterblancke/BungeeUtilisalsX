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
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerCommandCall implements CommandCall
{

    public static void sendToServer( final User user, final IProxyServer server )
    {
        if ( user.getServerName().equalsIgnoreCase( server.getName() ) )
        {
            user.sendLangMessage( "general-commands.server.alreadyconnected", "{server}", server.getName() );
            return;
        }

        user.sendToServer( server );
        user.sendLangMessage( "general-commands.server.connecting", "{server}", server.getName() );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            user.sendLangMessage( "general-commands.server.usage", "{server}", user.getServerName() );
            return;
        }

        if ( args.size() == 2 )
        {
            if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.permission-other" ) ) )
            {
                user.sendLangMessage( "no-permission" );
                return;
            }

            final Optional<User> optionalTarget = BuX.getApi().getUser( args.get( 0 ) );
            final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( args.get( 1 ) );

            if ( server == null )
            {
                user.sendLangMessage( "general-commands.server.notfound", "{server}", args.get( 1 ) );
                return;
            }

            if ( !optionalTarget.isPresent() )
            {
                if ( BuX.getApi().getBridgeManager().useBridging()
                        && BuX.getApi().getPlayerUtils().isOnline( args.get( 0 ) ) )
                {
                    final Map<String, String> object = Maps.newHashMap();

                    object.put( "user", args.get( 0 ) );
                    object.put( "server", server.getName() );

                    BuX.getApi().getBridgeManager().getBridge().sendTargetedMessage(
                            BridgeType.BUNGEE_BUNGEE,
                            null,
                            Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                            "USER_MOVE_SERVER",
                            object
                    );
                }
                else
                {
                    user.sendLangMessage( "offline" );
                }
            }
            else
            {
                final User target = optionalTarget.get();

                user.sendLangMessage(
                        "general-commands.server.sent-other",
                        "{user}", target.getName(),
                        "{server}", server.getName()
                );
                sendToServer( target, server );
            }
        }
        else
        {
            final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( args.get( 0 ) );

            if ( server == null )
            {
                user.sendLangMessage( "general-commands.server.notfound", "{server}", args.get( 0 ) );
                return;
            }

            sendToServer( user, server );
        }
    }
}
