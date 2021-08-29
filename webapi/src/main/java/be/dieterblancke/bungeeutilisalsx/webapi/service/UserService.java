package be.dieterblancke.bungeeutilisalsx.webapi.service;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService
{

    public User findByName( final String name )
    {
        if ( name.equals( "CONSOLE" ) )
        {
            return User.console();
        }

        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( name );

        return storage.isLoaded() ? User.of( storage ) : null;
    }

    public User findByUuid( final UUID uuid )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( uuid );

        return storage.isLoaded() ? User.of( storage ) : null;
    }
}
