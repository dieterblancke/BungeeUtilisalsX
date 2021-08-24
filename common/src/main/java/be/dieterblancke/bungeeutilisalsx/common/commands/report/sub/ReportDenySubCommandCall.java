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

package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.UserDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;

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
        final Dao dao = BuX.getApi().getStorageManager().getDao();
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

        final Optional<User> optionalUser = BuX.getApi().getUser( report.getReportedBy() );

        if ( optionalUser.isPresent() )
        {
            final User target = optionalUser.get();

            target.sendLangMessage(
                    "general-commands.report.deny.denied",
                    "{id}", report.getId(),
                    "{reported}", report.getUserName(),
                    "{staff}", user.getName()
            );
        }
        else if ( BuX.getApi().getPlayerUtils().isOnline( report.getReportedBy() ) )
        {
            BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                    report.getReportedBy(),
                    "general-commands.report.deny.denied",
                    "{id}", report.getId(),
                    "{reported}", report.getUserName(),
                    "{staff}", user.getName()
            ) );
        }
        else
        {
            BuX.getApi().getStorageManager().getDao().getOfflineMessageDao().sendOfflineMessage(
                    report.getReportedBy(),
                    new OfflineMessage(
                            null,
                            "general-commands.report.deny.denied",
                            "{id}", report.getId(),
                            "{reported}", report.getUserName(),
                            "{staff}", user.getName()
                    )
            );
        }
    }
}
