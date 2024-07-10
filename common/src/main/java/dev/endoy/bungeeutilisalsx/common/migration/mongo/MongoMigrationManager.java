package dev.endoy.bungeeutilisalsx.common.migration.mongo;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.migration.FileMigration;
import dev.endoy.bungeeutilisalsx.common.migration.Migration;
import dev.endoy.bungeeutilisalsx.common.migration.MigrationManager;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.Document;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class MongoMigrationManager implements MigrationManager
{

    @Override
    public void initialize()
    {
    }

    @Override
    @SneakyThrows
    public void migrate()
    {
        final List<Class<?>> classes = Utils.getClassesInPackage( "dev.endoy.bungeeutilisalsx.common.migration.mongo.migrations" );

        for ( Class<?> clazz : classes )
        {
            final int migrationId = Integer.parseInt(
                    clazz.getSimpleName().split( "_" )[0].replace( "v", "" )
            );
            final Migration migration = (Migration) clazz.getConstructor().newInstance();

            if ( !this.migrationExists( migrationId ) && migration.shouldRun() && ( migration instanceof MongoMigration ) )
            {
                BuX.getLogger().log( Level.INFO, "Executing migration " + clazz.getSimpleName() );
                migration.migrate();
                this.createMigration(
                        migrationId,
                        migration instanceof FileMigration ? "file" : "java",
                        migration.getClass().getName(),
                        new Date(),
                        true
                );
                BuX.getLogger().log( Level.INFO, "Successfully executed migration " + clazz.getSimpleName() );
            }
        }

    }

    private boolean migrationExists( final int version )
    {
        final MongoCollection<Document> migrationColl = db().getCollection( "bu_migrations" );

        return migrationColl.find( Filters.and(
                Filters.eq( "migration_id", version ),
                Filters.eq( "success", true )
        ) ).iterator().hasNext();
    }

    private void createMigration( final int migrationId, final String type, final String className, final Date createdAt, final boolean success )
    {
        final MongoCollection<Document> migrationColl = db().getCollection( "bu_migrations" );

        migrationColl.insertOne( new Document()
                .append( "migration_id", migrationId )
                .append( "type", type )
                .append( "script", className )
                .append( "created_at", createdAt )
                .append( "success", success ) );
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
