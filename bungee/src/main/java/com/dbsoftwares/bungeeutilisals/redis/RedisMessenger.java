package com.dbsoftwares.bungeeutilisals.redis;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import java.util.List;
import java.util.Map;

public class RedisMessenger {

    private static final String CHANNEL_PREFIX = "BUNGEEUTILISALSX_";
    private final List<String> channels = Lists.newArrayList();
    private final Map<String, RedisMessageHandler> messageHandlers = Maps.newHashMap();

    public RedisMessenger() {
        registerChannel(CHANNEL_PREFIX + "MAIN");
    }

    public void registerMessageHandler(final RedisMessageHandler handler) {
        final String channel = CHANNEL_PREFIX + handler.getClass().getSimpleName().toUpperCase();
        handler.setChannel(channel);

        messageHandlers.put(channel, handler);

        if (!channels.contains(channel)) {
            registerChannel(channel);
        }
    }

    private void registerChannel(final RedisMessageHandler handler) {
        registerChannel(handler.getChannel());
    }

    private void registerChannel(final String channel) {
        RedisBungee.getApi().registerPubSubChannels(channel);
        channels.add(channel);
    }
}
