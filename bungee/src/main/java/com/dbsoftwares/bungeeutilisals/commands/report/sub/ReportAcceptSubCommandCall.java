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
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.utils.other.Report;

import java.util.List;

public class ReportAcceptSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.report.accept.usage" );
            return;
        }

        if ( !MathUtils.isLong( args.get( 0 ) ) )
        {
            user.sendLangMessage( "no-number" );
            return;
        }

        final long id = Long.parseLong( args.get( 0 ) );
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final ReportsDao reportsDao = dao.getReportsDao();
        final UserDao userDao = dao.getUserDao();

        if ( !reportsDao.reportExists( id ) )
        {
            user.sendLangMessage( "general-commands.report.accept.not-found" );
            return;
        }
        final Report report = reportsDao.getReport( id );

        report.accept( user.getName() );

        user.sendLangMessage(
                "general-commands.report.accept.updated",
                "{id}", id
        );
    }
}
