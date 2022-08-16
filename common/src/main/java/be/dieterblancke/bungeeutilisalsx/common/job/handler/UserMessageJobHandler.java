package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

public class UserMessageJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeUserMessageJob( final UserMessageJob job )
    {
        job.getUser().ifPresent( user ->
        {
            if ( job.getMessage().contains( "\n" ) )
            {
                final String[] lines = job.getMessage().split( "\n" );

                for ( String line : lines )
                {
                    user.sendMessage( line );
                }
            }
            else
            {
                user.sendMessage( job.getMessage() );
            }
        } );
    }

    @JobHandler
    void executeUserLanguageMessageJob( final UserLanguageMessageJob job )
    {
        job.getUser().or( job::getUserByName ).ifPresent( user -> user.sendLangMessage(
                job.getLanguagePath(),
                job.isPrefix(),
                null,
                null,
                job.getPlaceholders()
        ) );
    }
}
