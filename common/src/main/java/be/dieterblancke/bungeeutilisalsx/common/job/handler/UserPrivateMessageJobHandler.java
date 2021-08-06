package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserPrivateMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ExecuteEventJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserPrivateMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

public class UserPrivateMessageJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeUserPrivateMessageJob( final UserPrivateMessageJob job )
    {
        job.getTargetUser().ifPresent( user ->
        {
            if ( user.isMsgToggled() )
            {
                executeJob( new UserLanguageMessageJob( job, "general-commands.msgtoggle.not-receiving-pms" ) );
                return;
            }

            if ( user.isVanished() )
            {
                executeJob( new UserLanguageMessageJob( job, "offline" ) );
                return;
            }

            if ( user.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( job.getUserName() ) ) )
            {
                executeJob( new UserLanguageMessageJob( job, "general-commands." + job.getType().toString().toLowerCase() + ".ignored" ) );
                return;
            }

            user.getStorage().setData( "MSG_LAST_USER", job.getUserName() );

            user.sendLangMessage(
                    "general-commands." + job.getType().toString().toLowerCase() + ".format.receive",
                    false,
                    Utils::c,
                    null,
                    "{sender}", job.getUserName(),
                    "{message}", job.getMessage()
            );

            executeJob( new UserLanguageMessageJob(
                    job,
                    false,
                    true,
                    "general-commands." + job.getType().toString().toLowerCase() + ".format.send",
                    "{receiver}", user.getName(),
                    "{message}", job.getMessage()
            ) );

            executeJob( new ExecuteEventJob(
                    UserPrivateMessageEvent.class,
                    job.getUserName(),
                    job.getTargetName(),
                    job.getMessage()
            ) );
        } );
    }
}
