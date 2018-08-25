package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.motd.MotdData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
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

        insertName(event.getConnection());

        for (final MotdData motdData : dataList) {
            if (motdData.isDef() || motdData.getConditionHandler().checkCondition(event.getConnection())) {
                final Version version = Version.getVersion(event.getConnection().getVersion());
                String motd = motdData.getMotd();

                motd = motd.replace("{user}", event.getConnection().getName() == null ? "Unknown" : event.getConnection().getName());
                motd = motd.replace("{version}", version == null ? "Unknown" : version.toString());

                if (event.getConnection().getVirtualHost() == null || event.getConnection().getVirtualHost().getHostName() == null) {
                    motd = motd.replace("{domain}", "Unknown");
                } else {
                    motd = motd.replace("{domain}", event.getConnection().getVirtualHost().getHostName());
                }
                motd = PlaceHolderAPI.formatMessage(motd);

                final BaseComponent component = new TextComponent(Utils.format(motd));

                event.getResponse().setDescriptionComponent(component);
                break;
            }
        }
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private void insertName(PendingConnection connection) {
        try {
            final String name = BUCore.getApi().getStorageManager().getDao().getUserDao()
                    .getUsersOnIP(Utils.getIP(connection.getAddress())).stream().findFirst().orElse(null);

            if (name == null) {
                return;
            }
            final Field nameField = ReflectionUtils.getField(connection.getClass(), "name");

            if (nameField != null) {
                nameField.set(connection, name);
            }
        } catch (Exception e) {
            // do nothing
        }
    }
}
