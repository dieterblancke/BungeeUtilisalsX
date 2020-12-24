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
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.UnicodeTranslator;

import java.util.List;

public class ReportHistorySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final ReportsDao reportsDao = BuX.getApi().getStorageManager().getDao().getReportsDao();
        final List<Report> reports = reportsDao.getReportsHistory( user.getName() );
        final int page;

        if ( args.size() == 0 )
        {
            page = 1;
        }
        else
        {
            if ( args.size() == 1 && MathUtils.isInteger( args.get( 0 ) ) )
            {
                page = Integer.parseInt( args.get( 0 ) );
            }
            else
            {
                page = 1;
            }
        }

        final String accepted = UnicodeTranslator.translate(
                user.getLanguageConfig().getString( "general-commands.report.history.accepted" )
        );
        final String denied = UnicodeTranslator.translate(
                user.getLanguageConfig().getString( "general-commands.report.history.denied" )
        );

        if ( reports.isEmpty() )
        {
            user.sendLangMessage( "general-commands.report.history.no-reports" );
            return;
        }

        try
        {
            final List<Report> pageReports = PageUtils.getPageFromList( page, reports, 10 );
            final int maxPages = MathUtils.ceil( reports.size() / 10 );

            user.sendLangMessage(
                    "general-commands.report.history.header",
                    "{page}", page,
                    "{maxPages}", maxPages
            );

            for ( Report report : pageReports )
            {
                user.sendLangMessage(
                        "general-commands.report.history.item",
                        "{id}", report.getId(),
                        "{reported}", report.getUserName(),
                        "{reporter}", report.getReportedBy(),
                        "{reason}", report.getReason(),
                        "{server}", report.getServer(),
                        "{date}", Dao.formatDateToString( report.getDate() ),
                        "{handled}", report.isHandled(),
                        "{accepted_sign}", report.isHandled() ? report.isAccepted() ? accepted : denied : ""
                );
            }

            user.sendLangMessage(
                    "general-commands.report.history.footer",
                    "{page}", page,
                    "{maxPages}", maxPages
            );
        }
        catch ( PageUtils.PageNotFoundException e )
        {
            user.sendLangMessage(
                    "general-commands.report.history.wrong-page",
                    "{page}", e.getPage(),
                    "{maxpages}", e.getMaxPages()
            );
        }
    }
}
