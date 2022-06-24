package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserFriendPrivateMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ExecuteEventJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserFriendPrivateMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

public class UserFriendPrivateMessageJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeUserFriendPrivateMessageJob( final UserFriendPrivateMessageJob job )
    {
        job.getTargetUser().ifPresent( user ->
        {
            if ( user.isMsgToggled() )
            {
                executeJob( new UserLanguageMessageJob( job, "general-commands.msgtoggle.not-receiving-pms" ) );
                return;
            }

            if ( !user.getFriendSettings().getSetting( FriendSetting.MESSAGES ) )
            {
                executeJob( new UserLanguageMessageJob( job, "friends." + job.getType().toString().toLowerCase() + ".disallowed" ) );
                return;
            }

            if ( user.isVanished() )
            {
                executeJob( new UserLanguageMessageJob( job, "offline" ) );
                return;
            }

            if ( user.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( job.getUserName() ) ) )
            {
                executeJob( new UserLanguageMessageJob( job, "friends." + job.getType().toString().toLowerCase() + ".ignored" ) );
                return;
            }

            user.getStorage().setData( UserStorageKey.FRIEND_MSG_LAST_USER, job.getUserName() );

            user.sendLangMessage(
                    false,
                    "friends." + job.getType().toString().toLowerCase() + ".format.receive",
                    "{sender}", job.getUserName(),
                    "{message}", job.getMessage()
            );

            executeJob( new UserLanguageMessageJob(
                    job,
                    false,
                    "friends." + job.getType().toString().toLowerCase() + ".format.send",
                    "{receiver}", user.getName(),
                    "{message}", job.getMessage()
            ) );

            executeJob( new ExecuteEventJob(
                    UserFriendPrivateMessageEvent.class,
                    job.getUserName(),
                    job.getTargetName(),
                    job.getMessage()
            ) );
        } );
    }
}
