package com.dbsoftwares.bungeeutilisals.utils.redis;

/*
 * Created by DBSoftwares on 23 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RedisMessenger implements Listener {

    private Gson gson = new Gson();

    public RedisMessenger() {
        for (Channels channel : Channels.values()) {
            RedisBungee.getApi().registerPubSubChannels(channel.toString());
        }
    }

    public void sendChannelMessage(Channels channel, Object message) {
        RedisBungee.getApi().sendChannelMessage(channel.toString(), gson.toJson(message));
    }

    @EventHandler
    public void onPubSubEvent(PubSubMessageEvent event) {
        for (Channels channel : Channels.values()) {
            if (channel.toString().equalsIgnoreCase(event.getChannel())) {
                channel.execute(event.getMessage());
            }
        }
    }
}