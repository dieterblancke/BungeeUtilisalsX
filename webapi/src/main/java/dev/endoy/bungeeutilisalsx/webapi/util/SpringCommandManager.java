package dev.endoy.bungeeutilisalsx.webapi.util;

import dev.endoy.bungeeutilisalsx.common.api.command.CommandBuilder;
import dev.endoy.bungeeutilisalsx.common.commands.CommandManager;
import dev.endoy.bungeeutilisalsx.webapi.commands.ApiTokenCommandCall;
import dev.endoy.bungeeutilisalsx.webapi.commands.CacheCommandCall;
import dev.endoy.bungeeutilisalsx.webapi.commands.SqlCommandCall;

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
