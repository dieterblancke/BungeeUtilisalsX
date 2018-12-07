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

package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.motd.MotdData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
import java.util.List;

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
