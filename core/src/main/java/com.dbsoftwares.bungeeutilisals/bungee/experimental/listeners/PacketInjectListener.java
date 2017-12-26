package com.dbsoftwares.bungeeutilisals.bungee.experimental.listeners;

import com.dbsoftwares.bungeeutilisals.api.experimental.encoder.CustomDecoder;
import com.dbsoftwares.bungeeutilisals.api.experimental.encoder.CustomEncoder;
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

public class PacketInjectListener implements Listener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer p = event.getPlayer();
        ServerConnection server = (ServerConnection) event.getServer();

        if (p != null && server != null) {
            ChannelWrapper wrapper = server.getCh();
            if (wrapper != null) {
                try {
                    wrapper.getHandle().pipeline().addAfter(PipelineUtils.PACKET_DECODER, "custom-decoder", new CustomDecoder(true, p));
                    wrapper.getHandle().pipeline().addAfter(PipelineUtils.PACKET_ENCODER, "custom-encoder", new CustomEncoder(true, p));
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

            channel.pipeline().addAfter(PipelineUtils.PACKET_DECODER, "custom-decoder", new CustomDecoder(false, p));
            channel.pipeline().addAfter(PipelineUtils.PACKET_ENCODER, "custom-encoder", new CustomEncoder(false, p));
        } catch (Exception ignore) { }
    }
}