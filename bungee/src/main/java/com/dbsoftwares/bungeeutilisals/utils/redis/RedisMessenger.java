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

package com.dbsoftwares.bungeeutilisals.utils.redis;

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