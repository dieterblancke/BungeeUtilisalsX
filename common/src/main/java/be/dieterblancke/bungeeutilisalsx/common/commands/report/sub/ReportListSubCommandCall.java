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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.PageUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;

import java.util.List;

public class ReportListSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final ReportsDao reportsDao = BuX.getApi().getStorageManager().getDao().getReportsDao();
        final List<Report> reports;
        final int page;

        if ( args.size() == 0 )
        {
            reports = reportsDao.getActiveReports();
            page = 1;
        }
        else
        {
            final String action = args.get( 0 );

            if ( action.equalsIgnoreCase( "all" ) )
            {
                reports = reportsDao.getReports();
            }
            else if ( action.equalsIgnoreCase( "accepted" ) )
            {
                reports = reportsDao.getAcceptedReports();
            }
            else if ( action.equalsIgnoreCase( "denied" ) )
            {
                reports = reportsDao.getDeniedReports();
            }
            else
            {
                reports = reportsDao.getActiveReports();
            }

            if ( args.size() > 1 && MathUtils.isInteger( args.get( 1 ) ) )
            {
                page = Integer.parseInt( args.get( 1 ) );
            }
            else
            {
                page = 1;
            }
        }

        if ( reports.isEmpty() )
        {
            user.sendLangMessage( "general-commands.report.list.no-reports" );
            return;
        }

        try
        {
            final List<Report> pageReports = PageUtils.getPageFromList( page, reports, 10 );
            final int maxPages = MathUtils.ceil( reports.size() / 10 );

            user.sendLangMessage(
                    "general-commands.report.list.header",
                    "{page}", page,
                    "{maxPages}", maxPages
            );

            for ( Report report : pageReports )
            {
                user.sendLangMessage(
                        "general-commands.report.list.item",
                        "{id}", report.getId(),
                        "{reported}", report.getUserName(),
                        "{reporter}", report.getReportedBy(),
                        "{reason}", report.getReason(),
                        "{server}", report.getServer(),
                        "{date}", Dao.formatDateToString( report.getDate() ),
                        "{handled}", report.isHandled()
                );
            }
        }
        catch ( PageUtils.PageNotFoundException e )
        {
            user.sendLangMessage(
                    "general-commands.report.list.wrong-page",
                    "{page}", e.getPage(),
                    "{maxpages}", e.getMaxPages()
            );
        }
    }
}
