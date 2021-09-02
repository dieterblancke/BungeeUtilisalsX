package be.dieterblancke.bungeeutilisalsx.webapi.util;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.webapi.commands.ApiTokenCommandCall;
import be.dieterblancke.bungeeutilisalsx.webapi.commands.CacheCommandCall;
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
        this.buildCommand( "cache", CommandBuilder.builder()
                .enabled( true )
                .name( "cache" )
                .executable( new CacheCommandCall() )
        );
        this.buildCommand( "api-token", CommandBuilder.builder()
                .enabled( true )
                .name( "api-token" )
                .executable( new ApiTokenCommandCall() )
        );
    }
}
