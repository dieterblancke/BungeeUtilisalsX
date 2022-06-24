package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;

import java.util.List;
import java.util.Optional;

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
        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final ReportsDao reportsDao = dao.getReportsDao();

        reportsDao.getReport( id ).thenAccept( report ->
        {
            if ( report == null )
            {
                user.sendLangMessage( "general-commands.report.accept.not-found" );
                return;
            }

            report.accept( user.getName() );

            user.sendLangMessage(
                    "general-commands.report.accept.updated",
                    "{id}", id
            );

            final Optional<User> optionalUser = BuX.getApi().getUser( report.getReportedBy() );

            if ( optionalUser.isPresent() )
            {
                final User target = optionalUser.get();

                target.sendLangMessage(
                        "general-commands.report.deny.accepted",
                        "{id}", report.getId(),
                        "{reported}", report.getUserName(),
                        "{staff}", user.getName()
                );
            }
            else if ( BuX.getApi().getPlayerUtils().isOnline( report.getReportedBy() ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                        report.getReportedBy(),
                        "general-commands.report.deny.accepted",
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
                                "general-commands.report.deny.accepted",
                                "{id}", report.getId(),
                                "{reported}", report.getUserName(),
                                "{staff}", user.getName()
                        )
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Accepts a report with a given id. This will notify the reporter.";
    }

    @Override
    public String getUsage()
    {
        return "/report accept (id)";
    }
}
