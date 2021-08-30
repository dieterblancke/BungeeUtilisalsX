package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

public class MongoApiTokenDao implements ApiTokenDao
{

    @Override
    public void createApiToken( final ApiToken token )
    {
        final MongoCollection<Document> collection = db().getCollection( "bu_api_token" );

        collection.insertOne( new Document()
                .append( "api_token", token.getApiToken() )
                .append( "expire_date", token.getExpireDate() )
                .append( "permissions", token.getPermissions()
                        .stream()
                        .map( ApiPermission::toString )
                        .collect( Collectors.joining( "," ) ) )
        );
    }

    @Override
    public Optional<ApiToken> findApiToken( final String token )
    {
        final MongoCollection<Document> collection = db().getCollection( "bu_api_token" );
        final Iterator<Document> iterator = collection.find( Filters.and(
                Filters.eq( "api_token", token ),
                Filters.gte( "expire_date", new Date() )
        ) ).limit( 1 ).iterator();

        if ( iterator.hasNext() )
        {
            final Document document = iterator.next();

            return Optional.of( new ApiToken(
                    document.getString( "api_token" ),
                    document.getDate( "expire_date" ),
                    Arrays.stream( document.getString( "permissions" ).split( "," ) )
                            .map( ApiPermission::valueOf )
                            .collect( Collectors.toList() )
            ) );
        }

        return Optional.empty();
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
