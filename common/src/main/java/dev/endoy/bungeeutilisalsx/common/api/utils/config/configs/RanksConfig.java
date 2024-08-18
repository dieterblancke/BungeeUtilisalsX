package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import com.google.common.collect.Lists;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import dev.endoy.configuration.api.ISection;
import lombok.Getter;

import java.util.List;

public class RanksConfig extends Config
{

    @Getter
    private final List<StaffRankData> ranks = Lists.newArrayList();

    public RanksConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        ranks.clear();
    }

    @Override
    public void setup()
    {
        final List<ISection> sections = config.getSectionList( "ranks" );

        for ( ISection section : sections )
        {
            final String name = section.getString( "name" );
            final String display = section.getString( "display" );
            final String permission = section.getString( "permission" );
            final int priority = section.getInteger( "priority" );

            ranks.add( new StaffRankData( name, display, permission, priority ) );
        }
    }

    public StaffRankData getRankData( final String rankName )
    {
        return ranks.stream()
            .filter( rank -> rankName.equalsIgnoreCase( rank.getName() ) )
            .findFirst()
            .orElse( null );
    }
}
