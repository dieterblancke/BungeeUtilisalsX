package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.configuration.api.ISection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PunishmentsTracksConfig extends Config
{

    @Getter
    private final List<PunishmentTrack> punishmentTracks = new ArrayList<>();

    public PunishmentsTracksConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        punishmentTracks.clear();
    }

    @Override
    public void setup()
    {
        for ( ISection section : config.getSectionList( "tracks" ) )
        {
            final String identifier = section.getString( "identifier" );
            final boolean canRunAgain = section.getBoolean( "can-run-again" );
            final List<PunishmentTrackRecord> records = section.getSectionList( "track" )
                .stream()
                .map( track ->
                {
                    final int count = track.getInteger( "count" );
                    final String action = track.getString( "action" );

                    return new PunishmentTrackRecord( count, action );
                } )
                .collect( Collectors.toList() );

            this.punishmentTracks.add( new PunishmentTrack(
                identifier,
                canRunAgain,
                records
            ) );
        }
    }

    public PunishmentTrack getPunishmentTrack( final String reason )
    {
        return punishmentTracks
            .stream()
            .filter( track -> track.getIdentifier().equalsIgnoreCase( reason ) )
            .findFirst()
            .orElse( null );
    }

    private List<PunishmentType> getPunishmentTypes( final String str )
    {
        return str.equals( "*" )
            ? Arrays.asList( PunishmentType.values().clone() )
            : Collections.singletonList( PunishmentType.valueOf( str ) );
    }
}
