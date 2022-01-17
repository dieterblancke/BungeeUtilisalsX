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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface UserDao
{

    CompletableFuture<Void> createUser( UUID uuid, String username, String ip, Language language, String joinedHost );

    CompletableFuture<Void> createUser( UUID uuid, String username, String ip, Language language, Date login, Date logout, String joinedHost );

    CompletableFuture<Void> updateUser( UUID uuid, String name, String ip, Language language, Date logout );

    CompletableFuture<Boolean> exists( String name );

    CompletableFuture<Boolean> ipExists( String ip );

    CompletableFuture<Optional<UserStorage>> getUserData( UUID uuid );

    CompletableFuture<Optional<UserStorage>> getUserData( String name );

    CompletableFuture<List<String>> getUsersOnIP( String ip );

    CompletableFuture<Void> setName( UUID uuid, String name );

    CompletableFuture<Void> setLanguage( UUID uuid, Language language );

    CompletableFuture<Void> setJoinedHost( UUID uuid, String joinedHost );

    CompletableFuture<Map<String, Integer>> getJoinedHostList();

    CompletableFuture<Map<String, Integer>> searchJoinedHosts( final String searchTag );

    CompletableFuture<Void> ignoreUser( UUID user, UUID ignore );

    CompletableFuture<Void> unignoreUser( UUID user, UUID unignore );

    CompletableFuture<UUID> getUuidFromName( String targetName );

}
