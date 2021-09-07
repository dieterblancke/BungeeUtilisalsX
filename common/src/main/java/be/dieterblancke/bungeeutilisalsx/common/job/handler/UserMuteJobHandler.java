package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserMuteJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.List;

public class UserMuteJobHandler extends AbstractJobHandler
{

    @JobHandler
    void executeUserMuteJob( final UserMuteJob job )
    {
        job.getUsers().forEach( user -> this.muteUser(
                user,
                job.getLanguagePath(),
                job.getPlaceholders(),
                job.getPunishmentType(),
                job.getReason()
        ) );
    }

    private void muteUser( final User user,
                           final String languagePath,
                           final Object[] placeholders,
                           final PunishmentType punishmentType,
                           final String reason )
    {
        List<String> mute = null;
        if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
        {
            mute = BuX.getApi().getPunishmentExecutor().searchTemplate(
                    user.getLanguageConfig().getConfig(), punishmentType, reason
            );
        }
        if ( mute == null )
        {
            user.sendLangMessage( languagePath, placeholders );
        }
        else
        {
            mute.forEach( str -> user.sendRawColorMessage( Utils.replacePlaceHolders( user, str, placeholders ) ) );
        }

        // if CURRENT_MUTES key is abscent, the next time a user chats, BuX will fetch the mutes for that user
        user.getStorage().removeData( "CURRENT_MUTES" );
    }
}
