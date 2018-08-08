package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class MotdPingListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        System.out.println(event.getConnection().getVersion());

        Version
    }
}
