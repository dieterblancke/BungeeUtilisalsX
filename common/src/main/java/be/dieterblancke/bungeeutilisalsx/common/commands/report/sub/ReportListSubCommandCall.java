package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
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
                        MessagePlaceholders.create()
                                .append( "page", page )
                                .append( "maxPages", pages )
                                .append( "previousPage", previous )
                                .append( "nextPage", next )
                );

                for ( Report report : pageReports )
                {
                    user.sendLangMessage(
                            "general-commands.report.list.item",
                            MessagePlaceholders.create()
                                    .append( report )
                                    .append( "handled", user.getLanguageConfig().getConfig().getString( "general-commands.report.list." + ( report.isHandled() ? "handled" : "unhandled" ) ) )
                                    .append( "previousPage", previous )
                                    .append( "nextPage", next )
                    );
                }

                user.sendLangMessage(
                        "general-commands.report.list.footer",
                        MessagePlaceholders.create()
                                .append( "page", page )
                                .append( "maxPages", pages )
                                .append( "previousPage", previous )
                                .append( "nextPage", next )
                );
            }
            catch ( PageUtils.PageNotFoundException e )
            {
                user.sendLangMessage(
                        "general-commands.report.list.wrong-page",
                        MessagePlaceholders.create()
                                .append( "page", e.getPage() )
                                .append( "maxpages", e.getMaxPages() )
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
