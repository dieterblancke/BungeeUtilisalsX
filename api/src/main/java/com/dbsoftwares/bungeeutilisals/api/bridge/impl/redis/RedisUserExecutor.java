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

package com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.util.RedisUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import io.lettuce.core.api.async.RedisAsyncCommands;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

public class RedisUserExecutor implements EventExecutor
{

    private RedisBridge bridge;

    public RedisUserExecutor( final RedisBridge bridge )
    {
        this.bridge = bridge;

        ProxyServer.getInstance().getScheduler().schedule(
                BUCore.getApi().getPlugin(), new RedisUserCleanupTask( bridge ), 0, 1, TimeUnit.MINUTES
        );
    }

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        final RedisAsyncCommands<String, RedisUser> commands = this.bridge.getUserConnection().async();

        commands.hset( "bungeeutilisalsx:users", event.getUser().getName(), new RedisUser( event.getUser() ) );
    }

    @Event
    public void onUnload( final UserUnloadEvent event )
    {
        final RedisAsyncCommands<String, RedisUser> commands = this.bridge.getUserConnection().async();

        commands.hdel( "bungeeutilisalsx:users", event.getUser().getName() );
    }
}
