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
        job.getUser().ifPresent( user -> user.sendMessage( job.getMessage() ) );
    }

    @JobHandler
    void executeUserLanguageMessageJob( final UserLanguageMessageJob job )
    {
        job.getUser().ifPresent( user -> user.sendLangMessage(
                job.getLanguagePath(),
                job.isPrefix(),
                job.isColorBefore() ? Utils::c : null,
                null,
                job.getPlaceholders()
        ) );
    }
}
