package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.webapi.commands.SqlCommandCall;

public class SpringCommandManager extends CommandManager
{

    public void load()
    {
        if ( !commands.isEmpty() )
        {
            this.unregisterAll();
        }

        this.buildCommand( "sql", CommandBuilder.builder()
                .enabled( true )
                .name( "sql" )
                .executable( new SqlCommandCall() )
        );
    }
}
