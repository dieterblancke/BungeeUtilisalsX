package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.*;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao.*;
import lombok.Getter;

@Getter
public class MongoDao implements Dao
{

    private final UserDao userDao;
    private final PunishmentDao punishmentDao;
    private final FriendsDao friendsDao;
    private final ReportsDao reportsDao;
    private final OfflineMessageDao offlineMessageDao;
    private final ApiTokenDao apiTokenDao;

    public MongoDao()
    {
        this.userDao = new MongoUserDao();
        this.punishmentDao = new MongoPunishmentDao();
        this.friendsDao = new MongoFriendsDao();
        this.reportsDao = new MongoReportsDao();
        this.offlineMessageDao = new MongoOfflineMessageDao();
        this.apiTokenDao = new MongoApiTokenDao();
    }
}
