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

package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ProxyServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CheckIpCommand extends BUCommand
{

    private static final Pattern IP_PATTERN = Pattern.compile( "^(?:(?:^|\\.)(?:2(?:5[0-5]|[0-4]\\d)|1?\\d?\\d)){4}$" );

    public CheckIpCommand()
    {
        super(
                "checkip",
                Arrays.asList( ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.checkip.aliases" ).split( ", " ) ),
                ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.checkip.permission" )
        );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return null;
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length == 0 )
        {
            user.sendLangMessage( "punishments.checkip.usage" );
            return;
        }

        // Running it async ...
        ProxyServer.getInstance().getScheduler().runAsync( BungeeUtilisals.getInstance(), () ->
        {
            final Dao dao = BUCore.getApi().getStorageManager().getDao();
            final UserDao userDao = dao.getUserDao();
            final PunishmentDao punishmentDao = dao.getPunishmentDao();

            final boolean exists = IP_PATTERN.matcher( args[0] ).find() ? userDao.ipExists( args[0] ) : userDao.exists( args[0] );
            if ( !exists )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }
            final UserStorage storage = userDao.getUserData( args[0] );

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
                    if ( BUCore.getApi().getPlayerUtils().isOnline( u ) )
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