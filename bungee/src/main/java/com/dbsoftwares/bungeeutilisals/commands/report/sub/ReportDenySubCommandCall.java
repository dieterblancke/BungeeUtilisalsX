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
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.MessageQueue;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.utils.other.QueuedMessage;
import com.dbsoftwares.bungeeutilisalsx.common.utils.other.Report;

import java.util.List;
import java.util.Optional;

public class ReportDenySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.report.deny.usage" );
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
            user.sendLangMessage( "general-commands.report.deny.not-found" );
            return;
        }
        final Report report = reportsDao.getReport( id );

        reportsDao.handleReport( id, false );
        user.sendLangMessage(
                "general-commands.report.deny.updated",
                "{id}", report.getId()
        );

        final Optional<User> optionalUser = BUCore.getApi().getUser( report.getReportedBy() );
        final MessageQueue<QueuedMessage> queue;

        if ( optionalUser.isPresent() )
        {
            queue = optionalUser.get().getMessageQueue();
        }
        else
        {
            queue = BUCore.getApi().getStorageManager().getDao().createMessageQueue();
        }
        queue.add( new QueuedMessage(
                -1,
                report.getReportedBy(),
                new QueuedMessage.Message(
                        "general-commands.report.deny.denied",
                        "{id}", report.getId(),
                        "{reported}", report.getUserName(),
                        "{staff}", user.getName()
                ),
                "NAME"
        ) );
    }
}
