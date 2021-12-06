package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserUnmuteJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;

import java.util.List;

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao.useServerPunishments;

public class UserUnmuteJobHandler
{

    @JobHandler
    void handleUserUnmuteJob( final UserUnmuteJob job )
    {
        job.getUser().ifPresent( user ->
        {
            if ( !user.getStorage().hasData( UserStorageKey.CURRENT_MUTES ) )
            {
                return;
            }
            final List<PunishmentInfo> mutes = user.getStorage().getData( UserStorageKey.CURRENT_MUTES );

            mutes.removeIf( mute ->
            {
                if ( useServerPunishments() )
                {
                    return mute.getServer().equalsIgnoreCase( job.getServerName() );
                }
                else
                {
                    return true;
                }
            } );
        } );
    }
}
