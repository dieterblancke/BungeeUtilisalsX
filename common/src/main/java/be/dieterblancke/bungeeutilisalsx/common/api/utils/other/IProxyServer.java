package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.Collection;

public interface IProxyServer
{

    String getName();

    Collection<String> getPlayers();

    Collection<User> getUsers();

}
