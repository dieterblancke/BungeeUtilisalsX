package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserUnmuteJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;

import java.util.List;

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao.useServerPunishments;

public class UserUnmuteJobHandler
{

    @JobHandler
    void handleUserUnmuteJob( final UserUnmuteJob job )
    {
        job.getUser().ifPresent( user ->
        {
            if ( !user.getStorage().hasData( "CURRENT_MUTES" ) )
            {
                return;
            }
            final List<PunishmentInfo> mutes = user.getStorage().getData( "CURRENT_MUTES" );

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
