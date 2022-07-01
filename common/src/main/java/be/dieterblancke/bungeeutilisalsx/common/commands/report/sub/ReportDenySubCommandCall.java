package be.dieterblancke.bungeeutilisalsx.common.commands.report.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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

        reportsDao.getReport( id ).thenAccept( report ->
        {
            if ( report == null )
            {
                user.sendLangMessage( "general-commands.report.deny.not-found" );
                return;
            }

            reportsDao.handleReport( id, false );
            user.sendLangMessage(
                    "general-commands.report.deny.updated",
                    report
            );

            Optional<User> optionalUser = BuX.getApi().getUser( report.getReportedBy() );
            MessagePlaceholders placeholders = MessagePlaceholders.create()
                    .append( report )
                    .append( "staff", user.getName() );

            if ( optionalUser.isPresent() )
            {
                final User target = optionalUser.get();

                target.sendLangMessage(
                        "general-commands.report.deny.denied",
                        placeholders
                );
            }
            else if ( BuX.getApi().getPlayerUtils().isOnline( report.getReportedBy() ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                        report.getReportedBy(),
                        "general-commands.report.deny.denied",
                        placeholders
                ) );
            }
            else
            {
                BuX.getApi().getStorageManager().getDao().getOfflineMessageDao().sendOfflineMessage(
                        report.getReportedBy(),
                        new OfflineMessage(
                                null,
                                "general-commands.report.deny.denied",
                                placeholders
                        )
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Denies a report with a given id.";
    }

    @Override
    public String getUsage()
    {
        return "/report deny (id)";
    }
}
