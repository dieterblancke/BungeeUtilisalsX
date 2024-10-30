package dev.endoy.bungeeutilisalsx.webapi.service;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.webapi.caching.Cacheable;
import dev.endoy.bungeeutilisalsx.webapi.dto.User;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService
{

    @Cacheable
    @SneakyThrows
    public User findByName( final String name )
    {
        if ( name.equals( "CONSOLE" ) )
        {
            return User.console();
        }

        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( name ).join().orElse( null );

        return storage != null && storage.isLoaded() ? User.of( storage ) : null;
    }

    @Cacheable
    public User findByUuid( final UUID uuid )
    {
        return findByUuidUncached( uuid );
    }

    @SneakyThrows
    public User findByUuidUncached( final UUID uuid )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( uuid ).join().orElse( null );

        return storage != null && storage.isLoaded() ? User.of( storage ) : null;
    }
}
