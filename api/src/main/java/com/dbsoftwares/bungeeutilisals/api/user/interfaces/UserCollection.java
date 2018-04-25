package com.dbsoftwares.bungeeutilisals.api.user.interfaces;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface UserCollection extends Serializable, Collection<User> {

    /**
     * @param name Name to select on.
     * @return User which corresponds to the given name. Null if not present.
     */
    Optional<User> fromName(String name);

    /**
     * @param player Player to select on.
     * @return User which corresponds to the given player. Null if not present.
     */
    Optional<User> fromPlayer(ProxiedPlayer player);

    /**
     * @return Same as toArray but with User type specified
     */
    User[] toTypeArray();

    /**
     * @return An array of names of the users in the list.
     */
    String[] toNameArray();
}