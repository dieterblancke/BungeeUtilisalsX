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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PunishmentListener implements Listener
{

    @EventHandler
    public void onLogin( LoginEvent event )
    {
        final PendingConnection connection = event.getConnection();
        final UUID uuid = connection.getUniqueId();
        final String ip = Utils.getIP( connection.getAddress() );

        final BUAPI api = BUCore.getApi();
        final UserStorage storage = api.getStorageManager().getDao().getUserDao().getUserData( uuid );
        final Language language = storage.getLanguage() == null ? api.getLanguageManager().getDefaultLanguage() : storage.getLanguage();

        final IConfiguration config = api.getLanguageManager().getConfig(
                BungeeUtilisals.getInstance().getDescription().getName(), language
        );
        final BansDao bansDao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao();
        PunishmentInfo info = null;

        if ( BungeeUtilisals.getInstance().getConfig().getBoolean( "debug", true ) )
        {
            System.out.println( String.format( "Checking ban for UUID: %s and IP: %s", uuid.toString(), ip ) );
        }

        if ( bansDao.isBanned( uuid ) )
        {
            info = bansDao.getCurrentBan( uuid );
        }
        else if ( bansDao.isIPBanned( ip ) )
        {
            info = bansDao.getCurrentIPBan( ip );
        }
        if ( info != null )
        { // active punishment found
            if ( info.isTemporary() )
            {
                if ( info.getExpireTime() <= System.currentTimeMillis() )
                {
                    if ( info.getType().equals( PunishmentType.TEMPBAN ) )
                    {
                        bansDao.removeCurrentBan( uuid, "CONSOLE" );
                    }
                    else
                    {
                        bansDao.removeCurrentIPBan( ip, "CONSOLE" );
                    }
                    return;
                }
            }
            String kick = null;
            if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( info.getReason() ) )
            {
                kick = Utils.formatList(
                        BUCore.getApi().getPunishmentExecutor().searchTemplate( config, info.getType(), info.getReason() ),
                        "\n"
                );
            }
            if ( kick == null )
            {
                kick = Utils.formatList( config.getStringList( "punishments." + info.getType().toString().toLowerCase() + ".kick" ), "\n" );
            }
            kick = api.getPunishmentExecutor().setPlaceHolders( kick, info );

            event.setCancelled( true );
            event.setCancelReason( Utils.format( kick ) );
        }
    }
}