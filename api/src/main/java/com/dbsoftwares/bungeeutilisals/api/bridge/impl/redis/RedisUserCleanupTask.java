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
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@AllArgsConstructor
public class RedisUserCleanupTask implements Runnable
{

    private final RedisBridge bridge;

    @Override
    public void run()
    {
        final Map<String, RedisUser> allUsers = bridge.getAllRedisUsers();
        final Collection<String> actualOnlineUsers = RedisBungee.getApi().getHumanPlayersOnline();
        final List<String> usersToDelete = Lists.newArrayList();

        for ( String user : allUsers.keySet() )
        {
            boolean found = false;
            for ( String onlineUser : actualOnlineUsers )
            {
                if ( user.equalsIgnoreCase( onlineUser ) )
                {
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                usersToDelete.add( user );
            }
        }

        if ( !usersToDelete.isEmpty() )
        {
            if ( ConfigFiles.CONFIG.getConfig().getBoolean( "debug" ) )
            {
                BUCore.getLogger().info( "Cleaning up " + usersToDelete.size() + " users from the redis user hash." );
            }

            final Future<Long> future = this.bridge.getUserConnection().async().hdel( "bungeeutilisalsx:users", usersToDelete.toArray( new String[0] ) );

            if ( ConfigFiles.CONFIG.getConfig().getBoolean( "debug" ) )
            {
                try
                {
                    BUCore.getLogger().info( "Successfully cleaned up " + future.get() + " users from the redis user hash." );
                }
                catch ( InterruptedException | ExecutionException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
