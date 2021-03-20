/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.CountNormalPunishments;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.TrackAction;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import com.dbsoftwares.configuration.api.ISection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
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
            final CountNormalPunishments countNormalPunishments = this.getCountNormalPunishmentsSettings( section );
            final int maxRuns = section.getInteger( "max-runs" );
            final TrackAction limitReachedAction = new TrackAction(
                    PunishmentType.valueOf( section.getString( "limit-reached-action.type" ).toUpperCase() ),
                    section.getString( "limit-reached-action.duration" )
            );
            final List<PunishmentTrackRecord> records = section.getSectionList( "track" )
                    .stream()
                    .map( track ->
                    {
                        final int count = track.getInteger( "count" );
                        final PunishmentType punishmentType = PunishmentType.valueOf( track.getString( "action.type" ).toUpperCase() );
                        final String duration = track.getString( "action.duration" );

                        return new PunishmentTrackRecord( count, new TrackAction( punishmentType, duration ) );
                    } )
                    .collect( Collectors.toList() );

            this.punishmentTracks.add( new PunishmentTrack(
                    identifier,
                    countNormalPunishments,
                    maxRuns,
                    limitReachedAction,
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

    private CountNormalPunishments getCountNormalPunishmentsSettings( final ISection section )
    {
        return new CountNormalPunishments(
                section.getBoolean( "count-normal-punishments.enabled" ),
                section.isString( "count-normal-punishments.types" )
                        ? Arrays.asList( PunishmentType.values().clone() )
                        : section.getStringList( "count-normal-punishments.types" ).stream().map( PunishmentType::valueOf ).collect( Collectors.toList() )
        );
    }
}
