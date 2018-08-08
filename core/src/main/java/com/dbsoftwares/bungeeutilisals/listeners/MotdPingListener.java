package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.motd.MotdData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class MotdPingListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        final List<MotdData> dataList = FileLocation.MOTD.getDataList();

        for (MotdData motdData : dataList) {
            if (motdData.isDef() || motdData.getConditionHandler().checkCondition(event.getConnection())) {
                Version version = Version.getVersion(event.getConnection().getVersion());
                String motd = motdData.getMotd();

                motd = motd.replace("{user}", event.getConnection().getName() == null ? "Unknown" : event.getConnection().getName());
                motd = motd.replace("{version}", version == null ? "Unknown" : version.toString());

                BaseComponent component = new TextComponent(Utils.format(motd));

                event.getResponse().setDescriptionComponent(component);
                break;
            }
        }
    }
}
