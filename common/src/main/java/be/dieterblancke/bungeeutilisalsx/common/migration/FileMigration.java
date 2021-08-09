package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class FileMigration implements Migration
{

    private final List<String> migrationStatements = new ArrayList<>();

    public FileMigration( final String filePath )
    {
        try ( InputStream is = AbstractBungeeUtilisalsX.class.getClassLoader().getResourceAsStream( filePath ) )
        {
            try ( BufferedReader reader = new BufferedReader( new InputStreamReader( is, StandardCharsets.UTF_8 ) ) )
            {
                StringBuilder builder = new StringBuilder();

                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    builder.append( line );

                    if ( line.endsWith( ";" ) )
                    {
                        builder.deleteCharAt( builder.length() - 1 );

                        String statement = SqlConstants.replaceConstants( builder.toString().trim() );
                        if ( !statement.isEmpty() )
                        {
                            this.migrationStatements.add( statement );
                        }

                        builder = new StringBuilder();
                    }
                }
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public void migrate( final Connection connection ) throws SQLException
    {
        try ( Statement statement = connection.createStatement() )
        {
            for ( String migrationStatement : migrationStatements )
            {
                statement.execute( migrationStatement );
            }
        }
    }
}
