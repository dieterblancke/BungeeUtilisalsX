package be.dieterblancke.bungeeutilisalsx.spigot.utils.friend;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FriendGuiUtils
{

    // This cache is really just to avoid people spamming the database by spam opening the friends gui
    private static final LoadingCache<UUID, List<FriendData>> USER_FRIENDS_CACHE = CacheHelper.<UUID, List<FriendData>>builder()
            .build(
                    builder -> builder.expireAfterWrite( 15, TimeUnit.SECONDS ),
                    new CacheLoader<UUID, List<FriendData>>()
                    {
                        @Override
                        public List<FriendData> load( final UUID uuid )
                        {
                            return BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( uuid );
                        }
                    }
            );

    public static List<FriendData> getFriendData( final UUID playerUuid )
    {
        try
        {
            return USER_FRIENDS_CACHE.get( playerUuid );
        }
        catch ( ExecutionException e )
        {
            return BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( playerUuid );
        }
    }

    public static FriendData getFriendData( final UUID playerUuid, final String name )
    {
        final List<FriendData> friends = getFriendData( playerUuid );

        return friends.stream()
                .filter( d -> d.getFriend().equalsIgnoreCase( name ) )
                .findFirst()
                .orElse( friends.stream().findFirst().orElse( null ) );
    }
}
