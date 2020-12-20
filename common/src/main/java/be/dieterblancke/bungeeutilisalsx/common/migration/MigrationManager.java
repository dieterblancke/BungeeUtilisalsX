package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;

public class MigrationManager
{

    public void initialize()
    {
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection() )
        {
            final DatabaseMetaData metaData = connection.getMetaData();

            try ( ResultSet rs = metaData.getTables( null, null, "bu_migrations", null ) )
            {
                if ( !rs.next() )
                {
                    final FileMigration fileMigration = new FileMigration( "migrations/migration_setup.sql" )
                    {
                        @Override
                        public boolean shouldRun( Connection connection )
                        {
                            return true;
                        }
                    };

                    fileMigration.migrate( connection );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not initialize migrations table", e );
        }
    }

    public void migrate() throws SQLException
    {
        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection() )
        {
            // Tried using Reflections & ClassPath (guava), neither seemed to work.
            for ( int i = 1; i < Integer.MAX_VALUE; i++ )
            {
                try
                {
                    final Class<?> clazz = Class.forName(
                            "be.dieterblancke.bungeeutilisalsx.common.migration.migrations.v" + i + "_migration"
                    );

                    final Migration migration = (Migration) clazz.newInstance();

                    if ( !this.migrationExists( i ) && migration.shouldRun( connection ) )
                    {
                        BuX.getLogger().log( Level.INFO, "Executing migration " + clazz.getSimpleName() );
                        migration.migrate( connection );
                        this.createMigration(
                                i,
                                migration instanceof FileMigration ? "file" : "java",
                                migration.getClass().getName(),
                                new Date(),
                                true
                        );
                        BuX.getLogger().log( Level.INFO, "Successfully executed migration " + clazz.getSimpleName() );
                    }
                }
                catch ( ClassNotFoundException e )
                {
                    break;
                }
                catch ( IllegalAccessException | InstantiationException e )
                {
                    BuX.getLogger().log( Level.SEVERE, "Could not execute migration", e );
                }
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
