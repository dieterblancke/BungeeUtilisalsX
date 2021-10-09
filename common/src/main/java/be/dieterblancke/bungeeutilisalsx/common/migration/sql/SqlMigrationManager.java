package be.dieterblancke.bungeeutilisalsx.common.migration.sql;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;
import be.dieterblancke.bungeeutilisalsx.common.migration.Migration;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class SqlMigrationManager implements MigrationManager
{

    @Override
    public void initialize()
    {
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection() )
        {
            final DatabaseMetaData metaData = connection.getMetaData();

            try ( ResultSet rs = metaData.getTables( null, null, "bu_migrations", null ) )
            {
                if ( !rs.next() )
                {
                    final FileMigration fileMigration = new FileMigration( "/migrations/migration_setup.sql" )
                    {
                        @Override
                        public boolean shouldRun()
                        {
                            return true;
                        }
                    };

                    fileMigration.migrate();
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not initialize migrations table", e );
        }
    }

    @Override
    public void migrate() throws SQLException
    {
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection() )
        {
            final List<Class<?>> classes = Utils.getClassesInPackage( "be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations" );

            try
            {
                for ( Class<?> clazz : classes )
                {
                    final int migrationId = Integer.parseInt(
                            clazz.getSimpleName().split( "_" )[0].replace( "v", "" )
                    );
                    final Migration migration = (Migration) clazz.newInstance();

                    if ( !this.migrationExists( migrationId ) && migration.shouldRun() )
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
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection();
              PreparedStatement ps = connection.prepareStatement(
                      "INSERT INTO bu_migrations(migration_id, type, script, created_at, success)" +
                              " VALUES (?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
              )
        )
        {
            ps.setInt( 1, migrationId );
            ps.setString( 2, type );
            ps.setString( 3, className );
            ps.setString( 4, Dao.formatDateToString( createdAt ) );
            ps.setBoolean( 5, success );

            ps.execute();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not check migration status", e );
        }
    }
}
