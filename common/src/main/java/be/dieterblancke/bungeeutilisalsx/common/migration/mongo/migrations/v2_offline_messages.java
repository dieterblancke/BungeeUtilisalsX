package be.dieterblancke.bungeeutilisalsx.common.migration.mongo.migrations;

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.migration.mongo.MongoMigration;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class v2_offline_messages implements MongoMigration
{
    @Override
    public boolean shouldRun() throws Exception
    {
        return true;
    }

    @Override
    public void migrate() throws Exception
    {
        db().getCollection( "bu_messagequeue" ).drop();
    }
}
