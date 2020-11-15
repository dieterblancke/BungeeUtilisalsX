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

package com.dbsoftwares.bungeeutilisals.commands.report.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.utils.other.Report;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReportCreateSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "general-commands.report.create.usage" );
            return;
        }

        final String targetName = args.get( 0 );
        final String reason = String.join( " ", args.subList( 1, args.size() ) );

        if ( !BUCore.getApi().getPlayerUtils().isOnline( targetName ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }
        final String bypassPermission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "report.bypass" );
        final Optional<User> optionalUser = BUCore.getApi().getUser( targetName );
        if ( optionalUser.isPresent() )
        {
            final User target = optionalUser.get();

            if ( target.hasPermission( bypassPermission ) )
            {
                user.sendLangMessage( "general-commands.report.create.bypassed" );
                return;
            }
        }

        final UUID targetUuid = BUCore.getApi().getPlayerUtils().getUuid( targetName );

        final Report report = new Report( -1, targetUuid, targetName, user.getName(), new Date(), user.getServerName(), reason, false, false );
        final ReportsDao reportsDao = BUCore.getApi().getStorageManager().getDao().getReportsDao();

        reportsDao.addReport( report );
        user.sendLangMessage( "general-commands.report.create.created", "{target}", targetName );

        BUCore.getApi().langPermissionBroadcast(
                "general-commands.report.create.broadcast",
                ConfigFiles.GENERALCOMMANDS.getConfig().getString( "report.subcommands.create.broadcast" ),
                "{target}", targetName,
                "{user}", user.getName(),
                "{reason}", reason,
                "{server}", user.getServerName()
        );
    }
}
