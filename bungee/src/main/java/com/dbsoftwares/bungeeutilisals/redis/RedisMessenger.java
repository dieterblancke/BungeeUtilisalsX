package com.dbsoftwares.bungeeutilisals.redis;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Map;

public class RedisMessenger implements Listener
{

    private static final String CHANNEL_PREFIX = "BUNGEEUTILISALSX_";
    private static final Gson gson = new Gson();
    private final List<String> channels = Lists.newArrayList();
    private final Map<String, RedisMessageHandler> messageHandlers = Maps.newHashMap();

    public RedisMessenger()
    {
        registerChannel( CHANNEL_PREFIX + "MAIN" );
    }

    @SuppressWarnings("unchecked")
    public void registerMessageHandlers( final Class<? extends RedisMessageHandler>... classes )
    {
        for ( Class<? extends RedisMessageHandler> clazz : classes )
        {
            try
            {
                registerMessageHandler( clazz.newInstance() );
            } catch ( InstantiationException | IllegalAccessException e )
            {
                BUCore.logException( e );
            }
        }
    }

    public RedisMessageHandler getHandler( final Class<? extends RedisMessageHandler> clazz )
    {
        return messageHandlers.values().stream()
                .filter( handler -> handler.getClass().equals( clazz ) )
                .findFirst()
                .orElse( null );
    }

    public void registerMessageHandler( final RedisMessageHandler handler )
    {
        final String channel = CHANNEL_PREFIX + handler.getClass().getSimpleName().toUpperCase();
        handler.setChannel( channel );

        messageHandlers.put( channel, handler );

        if ( !channels.contains( channel ) )
        {
            registerChannel( channel );
        }
    }

    private void registerChannel( final RedisMessageHandler handler )
    {
        registerChannel( handler.getChannel() );
    }

    private void registerChannel( final String channel )
    {
        RedisBungee.getApi().registerPubSubChannels( channel );
        channels.add( channel );
    }

    @EventHandler
    public void onPubSubEvent( PubSubMessageEvent event )
    {
        if ( !channels.contains( event.getChannel() ) )
        {
            return;
        }
        final RedisMessageHandler handler = messageHandlers.get( event.getChannel() );

        handler.handle( gson.fromJson( event.getMessage(), handler.getResultType() ) );
    }
}
