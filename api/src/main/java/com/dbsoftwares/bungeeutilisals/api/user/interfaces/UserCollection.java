/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

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