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
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import com.dbsoftwares.configuration.api.ISection;
import lombok.Getter;

import java.util.ArrayList;
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
            final List<PunishmentTrackRecord> records = section.getSectionList( "track" )
                    .stream()
                    .map( track ->
                    {
                        final int count = track.getInteger( "count" );
                        final PunishmentType punishmentType = PunishmentType.valueOf( track.getString( "action.type" ).toUpperCase() );
                        final String duration = track.getString( "action.duration" );

                        return new PunishmentTrackRecord( count, punishmentType, duration );
                    } )
                    .collect( Collectors.toList() );

            this.punishmentTracks.add( new PunishmentTrack( identifier, records ) );
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
}
