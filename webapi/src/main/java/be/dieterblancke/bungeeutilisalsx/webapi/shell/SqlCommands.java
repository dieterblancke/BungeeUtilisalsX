package be.dieterblancke.bungeeutilisalsx.webapi.shell;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import lombok.SneakyThrows;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlCommands
{

    @SneakyThrows
    @ShellMethod( "sql query" )
    public void query( final String query )
    {
        try ( Connection connection = BuX.getApi().getConnection();
              Statement statement = connection.createStatement() )
        {
            try ( ResultSet rs = statement.executeQuery( query ) )
            {
                while ( rs.next() )
                {
                    System.out.println( rs );
                }
            }
        }
    }

}
