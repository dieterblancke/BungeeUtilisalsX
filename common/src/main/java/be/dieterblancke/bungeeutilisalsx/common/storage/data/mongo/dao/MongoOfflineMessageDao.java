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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoOfflineMessageDao implements OfflineMessageDao
{

    private static final Gson GSON = new Gson();

    @Override
    public List<OfflineMessage> getOfflineMessages( final String username )
    {
        final List<OfflineMessage> messages = new ArrayList<>();
        final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );
        final FindIterable<Document> results = collection.find( Filters.and(
                Filters.eq( "username", username ),
                Filters.eq( "active", true )
        ) );

        for ( Document document : results )
        {
            messages.add( new OfflineMessage(
                    document.getLong( "_id" ),
                    document.getString( "message" ),
                    GSON.fromJson( document.getString( "parameters" ), Object[].class )
            ) );
        }

        return messages;
    }

    @Override
    public void sendOfflineMessage( final String username, final OfflineMessage message )
    {
        final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );

        collection.insertOne(
                new Document()
                        .append( "_id", manager().getNextSequenceValue( "offline_message_id" ) )
                        .append( "username", username )
                        .append( "message", message.getLanguagePath() )
                        .append( "parameters", GSON.toJson( message.getPlaceholders() ) )
                        .append( "active", true )
        );
    }

    @Override
    public void deleteOfflineMessage( final Long id )
    {
        final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );

        collection.deleteOne(
                Filters.eq( "_id", id )
        );
    }

    private MongoDBStorageManager manager()
    {
        return (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager();
    }

    private MongoDatabase db()
    {
        return manager().getDatabase();
    }
}
