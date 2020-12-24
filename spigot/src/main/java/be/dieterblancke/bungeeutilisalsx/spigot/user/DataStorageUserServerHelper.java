package be.dieterblancke.bungeeutilisalsx.spigot.user;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import be.dieterblancke.bungeeutilisalsx.spigot.api.user.UserServerHelper;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DataStorageUserServerHelper implements UserServerHelper
{

    private final LoadingCache<String, Optional<String>> userServerCache = CacheHelper.<String, Optional<String>>builder()
            .build(
                    builder -> builder.expireAfterWrite( 30, TimeUnit.SECONDS ),
                    new CacheLoader<String, Optional<String>>()
                    {
                        @Override
                        public Optional<String> load( final String userName )
                        {
                            return Optional.ofNullable(
                                    BuX.getApi().getStorageManager().getDao().getUserDao().getCurrentServersBulk(
                                            Collections.singletonList( userName )
                                    ).get( userName )
                            );
                        }
                    }
            );

    @Override
    public Optional<String> getCurrentServer( final String userName )
    {
        try
        {
            return userServerCache.get( userName );
        }
        catch ( ExecutionException e )
        {
            return Optional.empty();
        }
    }

    @Override
    public Map<String, Optional<String>> getCurrentServer( final List<String> users )
    {
        final Map<String, Optional<String>> cachedUsers = userServerCache.asMap();
        final List<String> usersToRequest = users.stream()
                .filter( user -> !cachedUsers.containsKey( user ) )
                .collect( Collectors.toList() );

        BuX.getApi().getStorageManager().getDao().getUserDao().getCurrentServersBulk( usersToRequest )
                .forEach( ( user, server ) -> this.userServerCache.put( user, Optional.ofNullable( server ) ) );

        final Map<String, Optional<String>> userServers = new HashMap<>();

        for ( String user : users )
        {
            userServers.put( user, this.getCurrentServer( user ) );
        }

        return userServers;
    }
}
