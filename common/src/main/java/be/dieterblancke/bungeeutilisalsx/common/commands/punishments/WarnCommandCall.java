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

package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;
import java.util.Optional;

public class WarnCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "punishments.warn.usage" );
            return;
        }
        if ( args.get( 0 ).equalsIgnoreCase( user.getName() ) )
        {
            user.sendLangMessage( "punishments.self-punishment" );
            return;
        }
        final String reason = Utils.formatList( args.subList( 1, args.size() ), " " );

        final Optional<User> optionalUser = BuX.getApi().getUser( args.get( 0 ) );
        if ( !optionalUser.isPresent() )
        {
            user.sendLangMessage( "offline" );
            return;
        }
        final User target = optionalUser.get();
        final UserStorage storage = target.getStorage();

        final UserPunishEvent event = new UserPunishEvent( PunishmentType.WARN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null );
        BuX.getApi().getEventLoader().launchEvent( event );

        if ( event.isCancelled() )
        {
            user.sendLangMessage( "punishments.cancelled" );
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();

        final PunishmentInfo info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().insertWarn(
                storage.getUuid(), storage.getUserName(), storage.getIp(), reason, user.getServerName(), user.getName()
        );

        List<String> warn = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            warn = BuX.getApi().getPunishmentExecutor().searchTemplate(
                    target.getLanguageConfig(), PunishmentType.WARN, reason
            );
        }
        if ( warn == null )
        {
            warn = target.getLanguageConfig().getStringList( "punishments.warn.onwarn" );
        }

        warn.forEach( str -> target.sendRawColorMessage( BuX.getApi().getPunishmentExecutor().setPlaceHolders( str, info ) ) );

        user.sendLangMessage( "punishments.warn.executed", executor.getPlaceHolders( info ).toArray( new Object[]{} ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast(
                        "punishments.warn.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                        "punishments.warn.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.warn.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        BuX.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.WARN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null
        ) );
    }
}