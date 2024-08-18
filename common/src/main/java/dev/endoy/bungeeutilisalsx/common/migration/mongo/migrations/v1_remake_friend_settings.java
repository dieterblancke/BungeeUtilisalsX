package dev.endoy.bungeeutilisalsx.common.migration.mongo.migrations;

import dev.endoy.bungeeutilisalsx.common.api.friends.FriendSetting;
import dev.endoy.bungeeutilisalsx.common.migration.mongo.MongoMigration;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class v1_remake_friend_settings implements MongoMigration
{
    @Override
    public boolean shouldRun() throws Exception
    {
        return true;
    }

    @Override
    public void migrate() throws Exception
    {
        final MongoCollection<Document> coll = db().getCollection( "bu_friendsettings" );
        final List<Document> documents = coll
            .find()
            .into( new ArrayList<>() )
            .stream()
            .flatMap( document -> Stream.of(
                new Document()
                    .append( "user", document.getString( "user" ) )
                    .append( "setting", FriendSetting.REQUESTS.toString() )
                    .append( "value", document.getBoolean( "requests" ) ),
                new Document()
                    .append( "user", document.getString( "user" ) )
                    .append( "setting", FriendSetting.MESSAGES.toString() )
                    .append( "value", document.getBoolean( "messages" ) )
            ) )
            .collect( Collectors.toList() );

        coll.deleteMany( new Document() );
        coll.insertMany( documents );
    }
}
