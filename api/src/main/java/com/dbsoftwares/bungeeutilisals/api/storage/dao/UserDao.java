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

package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserDao
{

    void createUser( UUID uuid, String username, String ip, Language language );

    void createUser( UUID uuid, String username, String ip, Language language, Date login, Date logout );

    void updateUser( UUID uuid, String name, String ip, Language language, Date logout );

    boolean exists( String name );

    boolean exists( UUID uuid );

    boolean ipExists( String ip );

    UserStorage getUserData( UUID uuid );

    UserStorage getUserData( String name );

    List<String> getUsersOnIP( String ip );

    Language getLanguage( UUID uuid );

    void setName( UUID uuid, String name );

    void setIP( UUID uuid, String ip );

    void setLanguage( UUID uuid, Language language );

    void setLogout( UUID uuid, Date logout );

    void ignoreUser( UUID user, UUID ignore );

    void unignoreUser( UUID user, UUID unignore );
}
