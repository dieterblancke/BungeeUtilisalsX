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
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class KickCommandCall extends PunishmentCommand
{

    public KickCommandCall()
    {
        super( "punishments.kick", false );
    }

    @Override
    public void onPunishmentExecute( final User user,
                                     final List<String> args,
                                     final List<String> parameters,
                                     final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();

        if ( !BuX.getApi().getPlayerUtils().isOnline( punishmentArgs.getPlayer() ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.KICK ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getKickAndWarnDao().insertKick(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                user.getName()
        ).thenAccept( info ->
        {

            // Attempting to kick if player is online. If briding is enabled and player is not online, it will attempt to kick on other bungee's.
            super.attemptKick( storage, "punishments.kick.onkick", info );
            user.sendLangMessage( "punishments.kick.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                            "punishments.kick.broadcast",
                            executor.getPlaceHolders( info ).toArray( new Object[]{} )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                            "punishments.kick.broadcast",
                            ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.kick.broadcast" ),
                            executor.getPlaceHolders( info ).toArray( new Object[]{} )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.KICK );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Kicks a user for a given reason.";
    }

    @Override
    public String getUsage()
    {
        return "/kick (user) <server> (reason)";
    }
}