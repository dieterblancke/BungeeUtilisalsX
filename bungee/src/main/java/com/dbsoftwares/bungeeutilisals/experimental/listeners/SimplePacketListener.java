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

package com.dbsoftwares.bungeeutilisals.experimental.listeners;

import com.dbsoftwares.bungeeutilisals.api.experimental.encoder.BUDecoder;
import com.dbsoftwares.bungeeutilisals.api.experimental.encoder.BUEncoder;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import io.netty.channel.Channel;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;

import java.lang.reflect.Method;

public class SimplePacketListener implements Listener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer p = event.getPlayer();
        ServerConnection server = (ServerConnection) event.getServer();

        if (p != null && server != null) {
            ChannelWrapper wrapper = server.getCh();
            if (wrapper != null) {
                try {
                    wrapper.getHandle().pipeline().addAfter(PipelineUtils.PACKET_DECODER, "custom-decoder", new BUDecoder(true, p));
                    wrapper.getHandle().pipeline().addAfter(PipelineUtils.PACKET_ENCODER, "custom-encoder", new BUEncoder(true, p));
                } catch (Exception ignore) { }
            }
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        try {
            ProxiedPlayer p = event.getPlayer();
            Object ch = ReflectionUtils.getField(p.getClass(), "ch").get(p);
            Method method = ReflectionUtils.getMethod(ch.getClass(), "getHandle", new Class[0]);
            Channel channel = (Channel) method.invoke(ch, new Object[0]);

            channel.pipeline().addAfter(PipelineUtils.PACKET_DECODER, "custom-decoder", new BUDecoder(false, p));
            channel.pipeline().addAfter(PipelineUtils.PACKET_ENCODER, "custom-encoder", new BUEncoder(false, p));
        } catch (Exception ignore) { }
    }
}