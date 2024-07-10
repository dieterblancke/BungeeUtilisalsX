package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentAction;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class PunishmentsActionsConfig extends Config
{

    @Getter
    private final Map<PunishmentType, List<PunishmentAction>> punishmentActions = Maps.newHashMap();

    public PunishmentsActionsConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        punishmentActions.clear();
    }

    @Override
    public void setup()
    {
        for ( ISection section : config.getSectionList( "actions" ) )
        {
            try
            {
                final String uid = section.getString( "uid" );
                final PunishmentType type = PunishmentType.valueOf( section.getString( "type" ) );

                try
                {
                    final TimeUnit unit = TimeUnit.valueOf( section.getString( "time.unit" ) );

                    if ( section.isInteger( "time.amount" ) )
                    {
                        final int amount = section.getInteger( "time.amount" );
                        final int limit = section.getInteger( "limit" );

                        final PunishmentAction action = new PunishmentAction( uid, type, unit, amount, limit, section.getStringList( "actions" ) );
                        final List<PunishmentAction> actions = punishmentActions.getOrDefault( type, Lists.newArrayList() );

                        actions.add( action );
                        punishmentActions.put( type, actions );
                    }
                    else
                    {
                        BuX.getLogger().warning( "An invalid number has been entered (" + section.getString( "time.amount" ) + ")." );
                    }
                }
                catch ( IllegalArgumentException e )
                {
                    BuX.getLogger().warning( "An invalid time unit has been entered (" + section.getString( "time.unit" ) + ")." );
                }
            }
            catch ( IllegalArgumentException e )
            {
                BuX.getLogger().warning( "An invalid punishment type has been entered (" + section.getString( "type" ) + ")." );
            }
        }
    }
}
