package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffJoinEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffLeaveEvent;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;

public class StaffNetworkExecutor implements EventExecutor
{

    @Event
    public void onJoin( final NetworkStaffJoinEvent event )
    {
        BuX.getInstance().getStaffMembers().add(
            new StaffUser( event.getUserName(), event.getUuid(), findStaffRank( event.getStaffRank() ) )
        );
    }

    @Event
    public void onLeave( final NetworkStaffLeaveEvent event )
    {
        BuX.getInstance().getStaffMembers().removeIf(
            staffUser -> staffUser.getName().equals( event.getUserName() )
        );
    }

    private StaffRankData findStaffRank( final String rankName )
    {
        return ConfigFiles.RANKS.getRanks().stream()
            .filter( rank -> rank.getName().equals( rankName ) )
            .findFirst()
            .orElseThrow( () -> new RuntimeException(
                "Could not find a staff rank called \"" + rankName + "\"."
                    + " If you are using redis, make sure the configs are synchronized."
            ) );
    }
}
