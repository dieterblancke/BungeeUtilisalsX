package be.dieterblancke.bungeeutilisalsx.common.migration.mongo;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;
import be.dieterblancke.bungeeutilisalsx.common.migration.Migration;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public void migrate()
    {
        final List<Class<?>> classes = Utils.getClassesInPackage( "be.dieterblancke.bungeeutilisalsx.common.migration.mongo.migrations" );

        try
        {
            for ( Class<?> clazz : classes )
            {
                final int migrationId = Integer.parseInt(
                        clazz.getSimpleName().split( "_" )[0].replace( "v", "" )
                );
                final Migration migration = (Migration) clazz.newInstance();

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
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not execute migration", e );
        }
    }

    private boolean migrationExists( final int version )
    {
        boolean exists = false;
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection();
              PreparedStatement ps = connection.prepareStatement(
                      "SELECT id FROM bu_migrations WHERE migration_id = ? AND success = ?;"
              )
        )
        {
            ps.setInt( 1, version );
            ps.setBoolean( 2, true );

            try ( ResultSet rs = ps.executeQuery() )
            {
                if ( rs.next() )
                {
                    exists = true;
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not check migration status", e );
        }
        return exists;
    }

    private void createMigration( final int migrationId, final String type, final String className, final Date createdAt, final boolean success )
    {
        final MongoDBStorageManager mongoDBStorageManager = (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager();
        final MongoCollection<Document> migrationColl = mongoDBStorageManager.getDatabase().getCollection( "bu_migrations" );

        migrationColl.insertOne( new Document()
                .append( "migration_id", migrationId )
                .append( "type", type )
                .append( "script", className )
                .append( "created_at", createdAt )
                .append( "success", success ) );
    }
}
