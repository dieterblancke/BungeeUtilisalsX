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

package com.dbsoftwares.bungeeutilisalsx.common.commands.punishments;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckIpCommandCall implements CommandCall
{

    private static final Pattern IP_PATTERN = Pattern.compile( "^(?:(?:^|\\.)(?:2(?:5[0-5]|[0-4]\\d)|1?\\d?\\d)){4}$" );

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.checkip.usage" );
            return;
        }

        // Running it async ...
        BuX.getInstance().getScheduler().runAsync( () ->
        {
            final Dao dao = BuX.getApi().getStorageManager().getDao();
            final UserDao userDao = dao.getUserDao();
            final PunishmentDao punishmentDao = dao.getPunishmentDao();

            final boolean exists = IP_PATTERN.matcher( args.get( 0 ) ).find() ? userDao.ipExists( args.get( 0 ) ) : userDao.exists( args.get( 0 ) );
            if ( !exists )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }
            final UserStorage storage = userDao.getUserData( args.get( 0 ) );

            user.sendLangMessage(
                    "punishments.checkip.head",
                    "{user}", storage.getUserName(), "{ip}", storage.getIp()
            );

            final List<String> users = userDao.getUsersOnIP( storage.getIp() );
            final List<String> formattedUsers = Lists.newArrayList();
            final Map<String, CheckIpStatus> statusMap = Maps.newHashMap();

            users.forEach( u ->
            {
                final UserStorage userStorage = userDao.getUserData( u );
                final boolean banned = punishmentDao.getBansDao().isBanned( storage.getUuid(), "ALL" );
                final boolean ipbanned = punishmentDao.getBansDao().isIPBanned( storage.getIp(), "ALL" );

                String colorPath = "punishments.checkip.colors.";
                if ( banned || ipbanned )
                {
                    colorPath += "banned";
                }
                else
                {
                    if ( BuX.getApi().getPlayerUtils().isOnline( u ) )
                    {
                        colorPath += "online";
                    }
                    else
                    {
                        colorPath += "offline";
                    }
                }

                formattedUsers.add(
                        Utils.c( user.getLanguageConfig().getString( colorPath ) )
                                + u
                );
            } );

            user.sendLangMessage(
                    "punishments.checkip.format.message",
                    "{players}", Utils.formatList(
                            formattedUsers, user.getLanguageConfig().getString( "punishments.checkip.format.separator" )
                    )
            );

            user.sendLangMessage(
                    "punishments.checkip.foot",
                    "{user}", storage.getUserName(), "{ip}", storage.getIp()
            );
        } );
    }

    private enum CheckIpStatus
    {
        ONLINE, OFFLINE, BANNED
    }
}