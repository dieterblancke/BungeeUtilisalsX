package dev.endoy.bungeeutilisalsx.common.job.handler;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.UserKickJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

public class UserKickJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeUserKickJob( final UserKickJob job )
    {
        job.getUsers().forEach( user -> this.kickUser(
            user,
            job.getLanguagePath(),
            job.getPlaceholders(),
            job.getPunishmentType(),
            job.getReason()
        ) );
    }

    private void kickUser( final User user,
                           final String languagePath,
                           final MessagePlaceholders placeholders,
                           final PunishmentType punishmentType,
                           final String reason )
    {
        String kick = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            kick = Utils.formatList( BuX.getApi().getPunishmentExecutor().searchTemplate(
                user.getLanguageConfig().getConfig(),
                punishmentType,
                reason
            ), "\n" );
        }
        if ( kick == null )
        {
            kick = Utils.formatList(
                user.getLanguageConfig().getConfig().getStringList( languagePath ),
                "\n"
            );
        }
        kick = Utils.replacePlaceHolders( user, kick, placeholders );
        user.kick( kick );
    }
}
