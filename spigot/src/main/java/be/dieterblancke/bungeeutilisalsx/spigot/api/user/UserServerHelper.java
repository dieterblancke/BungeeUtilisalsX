package be.dieterblancke.bungeeutilisalsx.spigot.api.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This interface is a layer that is used to fetch the current server of an user.
 * <p>
 * See {@link be.dieterblancke.bungeeutilisalsx.spigot.user.DataStorageUserServerHelper} for an implementation using UserDao
 * This is mostly meant for possible future implementations to fetch current status from redis, web API even, ...
 */
public interface UserServerHelper
{

    Optional<String> getCurrentServer( final String userName );

    Map<String, Optional<String>> getCurrentServer( final List<String> users );

}
