package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;

import java.util.Optional;
import java.util.function.Consumer;

public class UserUtils
{

    private UserUtils()
    {
    }

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        final long amount;

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            amount = BuX.getInstance().getRedisManager().getDataManager().getAmountOfOnlineUsersOnDomain( domain );
        }
        else
        {
            amount = BuX.getApi().getUsers().stream().filter( user -> user.getJoinedHost().equalsIgnoreCase( domain ) ).count();
        }

        return amount;
    }

    public static Optional<UserStorage> getUserStorage( final String userName, final Consumer<String> onFailure )
    {
        if ( !BuX.getApi().getPlayerUtils().isOnline( userName ) || StaffUtils.isHidden( userName ) )
        {
            onFailure.accept( "offline" );
            return Optional.empty();
        }
        final UserStorage target = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( userName ).join().orElse( null );
        if ( target == null || !target.isLoaded() )
        {
            onFailure.accept( "never-joined" );
            return Optional.empty();
        }
        return Optional.of( target );
    }
}
