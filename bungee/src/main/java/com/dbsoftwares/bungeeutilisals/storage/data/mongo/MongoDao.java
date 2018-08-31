package com.dbsoftwares.bungeeutilisals.storage.data.mongo;

import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoFriendsDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoPunishmentDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoUserDao;

/*
 * Created by DBSoftwares on 15 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoDao implements Dao {

    private UserDao userDao;
    private PunishmentDao punishmentDao;
    private FriendsDao friendsDao;

    public MongoDao() {
        this.userDao = new MongoUserDao();
        this.punishmentDao = new MongoPunishmentDao();
        this.friendsDao = new MongoFriendsDao();
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public PunishmentDao getPunishmentDao() {
        return punishmentDao;
    }

    @Override
    public FriendsDao getFriendsDao() {
        return friendsDao;
    }
}
