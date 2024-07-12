package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffJoinEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffLeaveEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ExecuteNetworkStaffEventJob;
import dev.endoy.bungeeutilisalsx.common.api.redis.IRedisDataManager;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.StaffUtils;

public class UserExecutor implements EventExecutor
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        event.getApi().addUser( event.getUser() );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            final IRedisDataManager redisDataManager = BuX.getInstance().getRedisManager().getDataManager();

            redisDataManager.loadRedisUser( event.getUser() );
        }
    }

    @Event
    public void onUnload( final UserUnloadEvent event )
    {
        event.getApi().removeUser( event.getUser() );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            final IRedisDataManager redisDataManager = BuX.getInstance().getRedisManager().getDataManager();

            redisDataManager.unloadRedisUser( event.getUser() );
        }
    }

    @Event
    public void onStaffLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();

        StaffUtils.getStaffRankForUser( user ).ifPresent( rank ->
        {
            BuX.getInstance().getJobManager().executeJob( new ExecuteNetworkStaffEventJob(
                    NetworkStaffJoinEvent.class,
                    user.getName(),
                    user.getUuid(),
                    rank.getName()
            ) );
        } );
    }

    @Event
    public void onStaffUnload( UserUnloadEvent event )
    {
        final User user = event.getUser();

        StaffUtils.getStaffRankForUser( user ).ifPresent( rank ->
        {
            BuX.getInstance().getJobManager().executeJob( new ExecuteNetworkStaffEventJob(
                    NetworkStaffLeaveEvent.class,
                    user.getName(),
                    user.getUuid(),
                    rank.getName()
            ) );
        } );
    }
}