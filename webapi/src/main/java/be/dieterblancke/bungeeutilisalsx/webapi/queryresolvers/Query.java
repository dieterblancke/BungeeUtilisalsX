package be.dieterblancke.bungeeutilisalsx.webapi.queryresolvers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
public class Query implements GraphQLQueryResolver
{

    public User findUserByName( final String name )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( name );

        return storage.isLoaded() ? User.of( storage ) : null;
    }

    public User findUserByUuid( final UUID uuid )
    {
        final UserStorage storage = BuX.getApi().getStorageManager().getDao().getUserDao().getUserData( uuid );

        return storage.isLoaded() ? User.of( storage ) : null;
    }
}
