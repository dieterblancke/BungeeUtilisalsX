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

package com.dbsoftwares.bungeeutilisals.redis.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.MessageData;

import java.util.Optional;

public class MsgMessageHandler extends RedisMessageHandler<MessageData>
{

    public MsgMessageHandler()
    {
        super( MessageData.class );
    }

    @Override
    public void handle( MessageData data )
    {
        final Optional<User> optional = BUCore.getApi().getUser( data.getReceiver() );

        if ( optional.isPresent() )
        {
            final User user = optional.get();

            user.getStorage().setData( "MSG_LAST_USER", data.getSenderName() );

            if ( data.getType().equals( "reply" ) )
            {
                String msgMessage = user.buildLangMessage( "general-commands.reply.format.receive" );
                msgMessage = Utils.c( msgMessage );
                msgMessage = msgMessage.replace( "{sender}", data.getSenderName() );
                msgMessage = msgMessage.replace( "{message}", data.getMessage() );

                user.sendRawMessage( msgMessage );
            }
            else
            {
                String msgMessage = user.buildLangMessage( "general-commands.msg.format.receive" );
                msgMessage = Utils.c( msgMessage );
                msgMessage = msgMessage.replace( "{sender}", data.getSenderName() );
                msgMessage = msgMessage.replace( "{message}", data.getMessage() );

                user.sendRawMessage( msgMessage );
            }
        }
    }
}
