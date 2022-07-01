package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UnicodeTranslator;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.PageUtils.PageNotFoundException;

import java.util.List;

public class ReportHistorySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        BuX.getApi().getStorageManager().getDao().getReportsDao().getReportsHistory( user.getName() ).thenAccept( reports ->
        {
            final int page = args.size() == 1 && MathUtils.isInteger( args.get( 0 ) )
                    ? Integer.parseInt( args.get( 0 ) )
                    : 1;
            final String accepted = UnicodeTranslator.translate(
                    user.getLanguageConfig().getConfig().getString( "general-commands.report.history.accepted" )
            );
            final String denied = UnicodeTranslator.translate(
                    user.getLanguageConfig().getConfig().getString( "general-commands.report.history.denied" )
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
                        MessagePlaceholders.create()
                                .append( "page", page )
                                .append( "maxPages", maxPages )
                );

                for ( Report report : pageReports )
                {
                    user.sendLangMessage(
                            "general-commands.report.history.item",
                            MessagePlaceholders.create()
                                    .append( report )
                                    .append( "accepted_sign", report.isHandled() ? report.isAccepted() ? accepted : denied : "" )
                    );
                }

                user.sendLangMessage(
                        "general-commands.report.history.footer",
                        MessagePlaceholders.create()
                                .append( "page", page )
                                .append( "maxPages", maxPages )
                );
            }
            catch ( PageNotFoundException e )
            {
                user.sendLangMessage(
                        "general-commands.report.history.wrong-page",
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
        return "Lists all reports you have created in the past.";
    }

    @Override
    public String getUsage()
    {
        return "/reports history [page]";
    }
}
