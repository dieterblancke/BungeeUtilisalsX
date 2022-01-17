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

package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class PunishmentListener implements Listener
{

    @EventHandler
    public void onLogin( final LoginEvent event )
    {
        final PendingConnection connection = event.getConnection();
        final UUID uuid = connection.getUniqueId();
        final String ip = Utils.getIP( (InetSocketAddress) connection.getSocketAddress() );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            System.out.printf( "Checking ban for UUID: %s and IP: %s for server ALL%n", uuid.toString(), ip );
        }

        final String kickReason = getKickReasonIfBanned( uuid, ip, "ALL" );

        if ( kickReason != null )
        {
            event.setCancelled( true );
            event.setCancelReason( Utils.format( kickReason ) );
        }
    }

    @EventHandler( priority = 127 )
    public void onConnect( final ServerConnectEvent event )
    {
        final ServerInfo target = event.getTarget();
        final ProxiedPlayer player = event.getPlayer();
        final String ip = Utils.getIP( (InetSocketAddress) player.getSocketAddress() );
        final String kickReason = getKickReasonIfBanned( player.getUniqueId(), ip, target.getName() );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            System.out.printf(
                    "Checking ban for UUID: %s and IP: %s for server %s%n",
                    player.getUniqueId().toString(),
                    ip,
                    target.getName()
            );
        }

        if ( kickReason != null )
        {
            event.setCancelled( true );

            // If current server is null, we're assuming the player just joined the network and tries to join a server he is banned on, kicking instead ...
            if ( event.getPlayer().getServer() == null )
            {
                player.disconnect( Utils.format( kickReason ) );
            }
            else
            {
                player.sendMessage( Utils.format( kickReason ) );
            }
        }
    }

    private String getKickReasonIfBanned( final UUID uuid, final String ip, final String server )
    {
        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final Optional<UserStorage> optionalStorage = dao.getUserDao().getUserData( uuid ).join();

        if ( !optionalStorage.isPresent() )
        {
            return null;
        }
        final UserStorage storage = optionalStorage.get();
        final IConfiguration config = BuX.getApi().getLanguageManager().getConfig(
                BuX.getInstance().getName(), storage.getLanguage()
        ).getConfig();

        final BansDao bansDao = dao.getPunishmentDao().getBansDao();
        PunishmentInfo info = bansDao.getCurrentBan( uuid, server ).join();
        if ( info == null )
        {
            info = bansDao.getCurrentIPBan( ip, server ).join();
        }

        if ( info == null )
        {
            return null;
        }

        if ( info.isExpired() )
        {
            if ( info.getType().equals( PunishmentType.TEMPBAN ) )
            {
                bansDao.removeCurrentBan( uuid, "EXPIRED", server );
            }
            else
            {
                bansDao.removeCurrentIPBan( ip, "EXPIRED", server );
            }
            return null;
        }

        String kick = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( info.getReason() ) )
        {
            kick = Utils.formatList(
                    BuX.getApi().getPunishmentExecutor().searchTemplate( config, info.getType(), info.getReason() ),
                    "\n"
            );
        }
        if ( kick == null )
        {
            kick = Utils.formatList( config.getStringList( "punishments." + info.getType().toString().toLowerCase() + ".kick" ), "\n" );
        }
        kick = BuX.getApi().getPunishmentExecutor().setPlaceHolders( kick, info );
        return kick;
    }
}