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
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReportListSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        loadReports( args.size() == 0 ? null : args.get( 0 ) ).thenAccept( reports ->
        {
            final int pages = (int) Math.ceil( (double) reports.size() / 10 );
            final int page;

            if ( args.size() > 1 && MathUtils.isInteger( args.get( 1 ) ) )
            {
                page = Math.min( Integer.parseInt( args.get( 1 ) ), pages );
            }
            else
            {
                page = 1;
            }


            if ( reports.isEmpty() )
            {
                user.sendLangMessage( "general-commands.report.list.no-reports" );
                return;
            }

            try
            {
                final List<Report> pageReports = PageUtils.getPageFromList( page, reports, 10 );
                final int previous = page > 1 ? page - 1 : 1;
                final int next = Math.min( page + 1, pages );

                user.sendLangMessage(
                        "general-commands.report.list.header",
                        "{page}", page,
                        "{maxPages}", pages,
                        "{previousPage}", previous,
                        "{nextPage}", next
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
                            "{handled}", user.getLanguageConfig().getConfig().getString( "general-commands.report.list." + ( report.isHandled() ? "handled" : "unhandled" ) ),
                            "{previousPage}", previous,
                            "{nextPage}", next
                    );
                }

                user.sendLangMessage(
                        "general-commands.report.list.footer",
                        "{page}", page,
                        "{maxPages}", pages,
                        "{previousPage}", previous,
                        "{nextPage}", next
                );
            }
            catch ( PageUtils.PageNotFoundException e )
            {
                user.sendLangMessage(
                        "general-commands.report.list.wrong-page",
                        "{page}", e.getPage(),
                        "{maxpages}", e.getMaxPages()
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Lists all active reports or all reports of a given type.";
    }

    @Override
    public String getUsage()
    {
        return "/report list [all / denied / accepted / active] [page]";
    }

    private CompletableFuture<List<Report>> loadReports( final String type )
    {
        final ReportsDao reportsDao = BuX.getApi().getStorageManager().getDao().getReportsDao();

        if ( type != null )
        {
            if ( type.equalsIgnoreCase( "all" ) )
            {
                return reportsDao.getReports();
            }
            else if ( type.equalsIgnoreCase( "accepted" ) )
            {
                return reportsDao.getAcceptedReports();
            }
            else if ( type.equalsIgnoreCase( "denied" ) )
            {
                return reportsDao.getDeniedReports();
            }
        }
        return reportsDao.getActiveReports();
    }
}
