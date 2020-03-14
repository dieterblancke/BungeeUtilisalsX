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

package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.util.BridgedUserMessage;

import java.util.Optional;

public class BungeeBridgeResponseHandler implements EventExecutor
{

    @Event
    public void onBridgeResponse( final BridgeResponseEvent event )
    {
        if ( event.getAction().equals( "USER" ) )
        {
            handleUserAction( event.asCasted( UserAction.class ) );
        }
    }

    private void handleUserAction( final UserAction action )
    {
        switch ( action.getType() )
        {
            case KICK:
            {
                final Optional<User> optionalUser = BUCore.getApi().getUser( action.getUser().getUuid() );
                if ( !optionalUser.isPresent() )
                {
                    return;
                }
                final User user = optionalUser.get();

                kickUser( user, action.getMessage() );
                break;
            }
            case KICK_IP:
            {
                BUCore.getApi().getUsers().stream()
                        .filter( user -> user.getIp().equalsIgnoreCase( action.getUser().getIp() ) )
                        .forEach( user -> kickUser( user, action.getMessage() ) );
                break;
            }
            case MESSAGE:
            {
                // TODO

                break;
            }
            case MUTE:
            {
                // TODO

                break;
            }
        }
    }

    private void kickUser( final User user, final BridgedUserMessage message )
    {
        final String reason = (String) message.getData().get( "reason" );

        String kick = null;
        if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            kick = Utils.formatList( BUCore.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig(), PunishmentType.BAN, reason
            ), "\n" );
        }
        if ( kick == null )
        {
            kick = Utils.formatList(
                    user.getLanguageConfig().getStringList( message.getMessage() ),
                    "\n"
            );
        }
        kick = LanguageUtils.replacePlaceHolders( user, kick, message.getPlaceholders() );
        user.kick( kick );
    }
}
