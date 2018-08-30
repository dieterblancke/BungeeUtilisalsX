package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public interface UserDao {

    void createUser(UUID uuid, String username, String ip, Language language);

    void createUser(UUID uuid, String username, String ip, Language language, Date login, Date logout);

    void updateUser(UUID uuid, String name, String ip, Language language, Date logout);

    boolean exists(String name);

    boolean exists(UUID uuid);

    UserStorage getUserData(UUID uuid);

    UserStorage getUserData(String name);

    List<String> getUsersOnIP(String ip);

    Language getLanguage(UUID uuid);


    void setName(UUID uuid, String name);

    void setIP(UUID uuid, String ip);

    void setLanguage(UUID uuid, Language language);

    void setLogout(UUID uuid, Date logout);
}
