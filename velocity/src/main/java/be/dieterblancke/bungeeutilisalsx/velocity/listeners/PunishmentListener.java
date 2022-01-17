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

package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.UUID;

public class PunishmentListener
{

    @Subscribe
    public void onLogin( final LoginEvent event )
    {
        final UUID uuid = event.getPlayer().getUniqueId();
        final String ip = Utils.getIP( event.getPlayer().getRemoteAddress() );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            System.out.printf( "Checking ban for UUID: %s and IP: %s for server ALL%n", uuid.toString(), ip );
        }

        final String kickReason = getKickReasonIfBanned( uuid, ip, "ALL" );

        if ( kickReason != null )
        {
            event.setResult( ResultedEvent.ComponentResult.denied( Component.text( Utils.c( kickReason ) ) ) );
        }
    }

    @Subscribe( order = PostOrder.LAST )
    public void onConnect( final ServerPreConnectEvent event )
    {
        event.getResult().getServer().ifPresent( target ->
        {
            final Player player = event.getPlayer();
            final String ip = Utils.getIP( player.getRemoteAddress() );
            final String kickReason = getKickReasonIfBanned( player.getUniqueId(), ip, target.getServerInfo().getName() );

            if ( ConfigFiles.CONFIG.isDebug() )
            {
                System.out.printf(
                        "Checking ban for UUID: %s and IP: %s for server %s%n",
                        player.getUniqueId().toString(),
                        ip,
                        target.getServerInfo().getName()
                );
            }

            if ( kickReason != null )
            {
                // If current server is null, we're assuming the player just joined the network and tries to join a server he is banned on, kicking instead ...
                if ( !event.getPlayer().getCurrentServer().isPresent() )
                {
                    player.disconnect( Component.text( Utils.c( kickReason ) ) );
                }
                else
                {
                    event.setResult( ServerPreConnectEvent.ServerResult.denied() );
                    player.sendMessage( Component.text( Utils.c( kickReason ) ) );
                }
            }
        } );
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
        final Language language = storage.getLanguage() == null ? BuX.getApi().getLanguageManager().getDefaultLanguage() : storage.getLanguage();
        final IConfiguration config = BuX.getApi().getLanguageManager().getConfig(
                BuX.getInstance().getName(), language
        ).getConfig();

        final BansDao bansDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao();
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