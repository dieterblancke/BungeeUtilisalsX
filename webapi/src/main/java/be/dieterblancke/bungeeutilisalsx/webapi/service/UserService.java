package be.dieterblancke.bungeeutilisalsx.webapi.service;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
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

        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( name ).get();

        return storage.isLoaded() ? User.of( storage ) : null;
    }

    @Cacheable
    public User findByUuid( final UUID uuid )
    {
        return findByUuidUncached( uuid );
    }

    @SneakyThrows
    public User findByUuidUncached( final UUID uuid )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( uuid ).get();

        return storage.isLoaded() ? User.of( storage ) : null;
    }
}
