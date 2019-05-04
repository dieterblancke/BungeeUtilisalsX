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

package com.dbsoftwares.bungeeutilisals.redis.handlers;

import com.dbsoftwares.bungeeutilisals.commands.general.StaffChatCommand;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.StaffChatData;

public class StaffChatMessageHandler extends RedisMessageHandler<StaffChatData> {

    public StaffChatMessageHandler() {
        super(StaffChatData.class);
    }

    @Override
    public void handle(StaffChatData data) {
        StaffChatCommand.sendStaffChatMessage(data.getServer(), data.getPlayer(), data.getMessage());
    }
}
