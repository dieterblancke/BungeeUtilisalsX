package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.motd.ConditionHandler;
import dev.endoy.bungeeutilisalsx.common.motd.MotdData;
import dev.endoy.bungeeutilisalsx.common.motd.handlers.DomainConditionHandler;
import dev.endoy.bungeeutilisalsx.common.motd.handlers.MultiConditionHandler;
import dev.endoy.bungeeutilisalsx.common.motd.handlers.NameConditionHandler;
import dev.endoy.bungeeutilisalsx.common.motd.handlers.VersionConditionHandler;
import dev.endoy.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public class MotdConfig extends Config
{

    private final List<MotdData> motds = Lists.newArrayList();

    public MotdConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        motds.clear();
    }

    @Override
    protected void setup()
    {
        if ( config == null )
        {
            return;
        }

        for ( ISection section : config.getSectionList( "motd" ) )
        {
            final String condition = section.getString( "condition" );
            final String motd = section.getString( "motd" );
            final List<String> hoverMessages = section.exists( "player-hover" )
                    ? section.getStringList( "player-hover" )
                    : Lists.newArrayList();
            final ConditionHandler handler = this.createConditionHandler( condition );

            this.motds.add( new MotdData( handler, handler == null, motd, hoverMessages ) );
        }
    }

    private ConditionHandler createConditionHandler( final String condition )
    {
        if ( condition.contains( "||" ) || condition.contains( "&&" ) )
        {
            return this.createMultiConditionHandler( condition );
        }

        if ( condition.toLowerCase().startsWith( "domain" ) )
        {
            return new DomainConditionHandler( condition );
        }
        else if ( condition.toLowerCase().startsWith( "version" ) )
        {
            return new VersionConditionHandler( condition );
        }
        else if ( condition.toLowerCase().startsWith( "name" ) )
        {
            return new NameConditionHandler( condition );
        }
        else
        {
            return null;
        }
    }

    private ConditionHandler createMultiConditionHandler( final String conditions )
    {
        final List<ConditionHandler> handlers = Lists.newArrayList();

        if ( conditions.contains( "||" ) )
        {
            for ( String condition : conditions.split( "\\|\\|" ) )
            {
                handlers.add( this.createConditionHandler( condition.trim() ) );
            }
        }
        else if ( conditions.contains( "&&" ) )
        {
            for ( String condition : conditions.split( "&&" ) )
            {
                handlers.add( this.createConditionHandler( condition.trim() ) );
            }
        }

        return new MultiConditionHandler( conditions, conditions.contains( "&&" ), handlers.toArray( new ConditionHandler[0] ) );
    }
}
