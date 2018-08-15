package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;

import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public interface UserDao {

    void createUser(String uuid, String username, String ip, String language);

    void updateUser(String uuid, String name, String ip, String language);

    boolean exists(String name);

    boolean exists(UUID uuid);

    UserStorage getUserData(UUID uuid);

    UserStorage getUserData(String name);

    List<String> getUsersOnIP(String name);

    Language getLanguage(UUID uuid);
}
