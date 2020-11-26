package com.dbsoftwares.bungeeutilisalsx.common.api.utils.other;

import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.Collection;

public interface IProxyServer
{

    String getName();

    Collection<String> getPlayers();

    Collection<User> getUsers();

}
