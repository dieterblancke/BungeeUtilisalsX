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

package com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis.codecs;

import com.dbsoftwares.bungeeutilisals.api.bridge.util.RedisUser;
import io.lettuce.core.codec.RedisCodec;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisUserCodec implements RedisCodec<String, RedisUser>
{

    @Override
    public String decodeKey( final ByteBuffer byteBuffer )
    {
        final byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get( bytes );

        return new String( bytes, StandardCharsets.UTF_8 );
    }

    @Override
    public RedisUser decodeValue( final ByteBuffer byteBuffer )
    {
        final byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get( bytes );

        RedisUser result = null;
        try ( ObjectInputStream input = new ObjectInputStream( new ByteArrayInputStream( bytes ) ) )
        {
            result = (RedisUser) input.readObject();
        }
        catch ( IOException | ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ByteBuffer encodeKey( final String s )
    {
        return ByteBuffer.wrap( s.getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public ByteBuffer encodeValue( final RedisUser redisUser )
    {
        ByteBuffer result = null;
        try ( ByteArrayOutputStream bos = new ByteArrayOutputStream();
              final ObjectOutputStream output = new ObjectOutputStream( bos ) )
        {
            output.writeObject( redisUser );
            output.flush();

            result = ByteBuffer.wrap( bos.toByteArray() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return result == null ? ByteBuffer.wrap( new byte[0] ) : result;
    }
}
