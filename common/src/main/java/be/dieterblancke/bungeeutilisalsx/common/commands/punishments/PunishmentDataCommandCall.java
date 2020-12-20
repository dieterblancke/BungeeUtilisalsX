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
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class PunishmentDataCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "punishments.punishmentdata.usage" );
            return;
        }

        final PunishmentType type = Utils.valueOfOr( args.get( 0 ).toUpperCase(), PunishmentType.BAN );
        final String id = args.get( 1 );

        if ( BuX.getApi().getStorageManager().getType().toString().contains( "SQL" ) && !MathUtils.isInteger( id ) )
        {
            user.sendLangMessage( "no-number" );
            return;
        }

        PunishmentInfo info = null;
        switch ( type )
        {
            case BAN:
            case TEMPBAN:
            case IPBAN:
            case IPTEMPBAN:
                info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getById( id );
                break;
            case MUTE:
            case TEMPMUTE:
            case IPMUTE:
            case IPTEMPMUTE:
                info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getById( id );
                break;
            case KICK:
                info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKickById( id );
                break;
            case WARN:
                info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarnById( id );
                break;
        }

        if ( info == null )
        {
            user.sendLangMessage(
                    "punishments.punishmentdata.not-found",
                    "{type}", type.toString().toLowerCase(),
                    "{id}", id
            );
        }
        else
        {
            user.sendLangMessage(
                    "punishments.punishmentdata.found",
                    BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray( new Object[0] )
            );
        }
    }
}
