package com.dbsoftwares.bungeeutilisals.storage.data.mongo;

import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
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

    public MongoDao() {
        this.userDao = new MongoUserDao();
        this.punishmentDao = new MongoPunishmentDao();
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public PunishmentDao getPunishmentDao() {
        return punishmentDao;
    }
}
