package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserPrivateMessageEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ExecuteEventJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserPrivateMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

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

            user.getStorage().setData( UserStorageKey.MSG_LAST_USER, job.getUserName() );

            user.sendLangMessage(
                    false,
                    "general-commands." + job.getType().toString().toLowerCase() + ".format.receive",
                    MessagePlaceholders.create()
                            .append( "sender", job.getUserName() )
                            .append( "message", job.getMessage() )
                            .append( "sender-server", BuX.getApi().getPlayerUtils().findPlayer( job.getUserName() ).getName() )
                            .append( "receiver-server", user.getServerName() )
            );

            executeJob( new UserLanguageMessageJob(
                    job,
                    false,
                    "general-commands." + job.getType().toString().toLowerCase() + ".format.send",
                    MessagePlaceholders.create()
                            .append( "receiver", user.getName() )
                            .append( "message", job.getMessage() )
                            .append( "sender-server", BuX.getApi().getPlayerUtils().findPlayer( job.getUserName() ).getName() )
                            .append( "receiver-server", user.getServerName() )
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
