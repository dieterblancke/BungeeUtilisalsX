package com.dbsoftwares.bungeeutilisals.redis;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class RedisMessageHandler<T>
{

    private static final Gson gson = new Gson();
    private final Class<?> resultType;
    private String channel;

    public abstract void handle( T data );

    public void send( final T data )
    {
        RedisBungee.getApi().sendChannelMessage( channel, gson.toJson( data ) );
    }
}
