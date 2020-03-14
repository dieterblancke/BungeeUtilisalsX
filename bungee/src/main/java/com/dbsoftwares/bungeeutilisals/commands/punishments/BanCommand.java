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

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserActionType;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.util.BridgedUserMessage;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BanCommand extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentArgs punishmentArgs = loadArguments( user, args, false );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( "punishments.ban.usage" + (useServerPunishments() ? "-server" : "") );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        if ( dao().getPunishmentDao().getBansDao().isBanned( storage.getUuid(), punishmentArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.ban.already-banned" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.BAN ) )
        {
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();

        final PunishmentInfo info = dao().getPunishmentDao().getBansDao().insertBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName()
        );

        final Optional<User> optionalTarget = BUCore.getApi().getUser( storage.getUserName() );

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            String kick = null;
            if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
            {
                kick = Utils.formatList( BUCore.getApi().getPunishmentExecutor().searchTemplate(
                        target.getLanguageConfig(), PunishmentType.BAN, reason
                ), "\n" );
            }
            if ( kick == null )
            {
                kick = Utils.formatList( target.getLanguageConfig().getStringList( "punishments.ban.kick" ), "\n" );
            }
            kick = executor.setPlaceHolders( kick, info );

            target.kick( kick );
        }
        else
        {
            if ( BUCore.getApi().getBridgeManager().useBungeeBridge() )
            {
                final Map<String, Object> data = Maps.newHashMap();
                data.put( "reason", reason );

                BUCore.getApi().getBridgeManager().getBungeeBridge().sendMessage(
                        BridgeType.BUNGEE_BUNGEE,
                        "USER",
                        new UserAction(
                                storage,
                                UserActionType.KICK,
                                new BridgedUserMessage(
                                        true,
                                        "punishments.ban.kick",
                                        data,
                                        executor.getPlaceHolders( info ).toArray()
                                )
                        )
                );
            }
        }

        user.sendLangMessage( "punishments.ban.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BUCore.getApi().langBroadcast(
                        "punishments.ban.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BUCore.getApi().langPermissionBroadcast(
                        "punishments.ban.broadcast",
                        FileLocation.PUNISHMENTS.getConfiguration().getString( "commands.ban.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        BUCore.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.BAN,
                user,
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                null
        ) );
    }
}