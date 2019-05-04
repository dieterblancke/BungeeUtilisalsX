/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.general.StaffChatCommand;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.redis.handlers.StaffChatMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.StaffChatData;
import com.dbsoftwares.configuration.api.IConfiguration;

public class StaffCharChatExecutor implements EventExecutor {

    @Event
    public void onChat(final UserChatEvent event) {
        final IConfiguration config = FileLocation.GENERALCOMMANDS.getConfiguration();
        final String detect = config.getString("staffchat.charchat.detect");
        if (!config.getBoolean("staffchat.enabled")
                || !config.getBoolean("staffchat.charchat.enabled")
                || !event.getMessage().startsWith(detect)) {
            return;
        }
        final User user = event.getUser();
        final String permission = config.getString("staffchat.permission");
        if (!user.getParent().hasPermission(permission) || user.isInStaffChat()) {
            return;
        }
        final String message = event.getMessage().substring(detect.length());
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            final RedisMessageHandler handler = BungeeUtilisals.getInstance().getRedisMessenger().getHandler(StaffChatMessageHandler.class);

            handler.send(new StaffChatData(user.getServerName(), user.getName(), event.getMessage()));
        } else {
            StaffChatCommand.sendStaffChatMessage(user.getServerName(), user.getName(), message);
        }

        event.setCancelled(true);
    }
}
