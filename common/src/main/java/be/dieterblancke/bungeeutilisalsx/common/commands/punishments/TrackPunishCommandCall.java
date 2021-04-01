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

package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TrackUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.List;

public class TrackPunishCommandCall extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentArgs punishmentArgs = loadArguments( user, args, false );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( "punishments.track.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        final String reason = punishmentArgs.getReason();
        final PunishmentTrack track = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( reason );
        final UserStorage storage = punishmentArgs.getStorage();

        if ( track == null )
        {
            user.sendLangMessage( "punishments.track.track-not-found" );
            return;
        }

        final List<PunishmentTrackInfo> trackInfos = dao().getPunishmentDao().getTracksDao().getTrackInfos(
                storage.getUuid(), track.getIdentifier(), punishmentArgs.getServerOrAll()
        );

        if ( TrackUtils.isFinished( track, trackInfos ) )
        {
            user.sendLangMessage( "punishments.track.track-already-finished" );
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();

        dao().getPunishmentDao().getTracksDao().addToTrack( new PunishmentTrackInfo(
                storage.getUuid(),
                track.getIdentifier(),
                punishmentArgs.getServerOrAll(),
                user.getName(),
                new Date(),
                true
        ) );

        dao().getPunishmentDao().getTracksDao().resetTrack(
                storage.getUuid(), track.getIdentifier(), punishmentArgs.getServerOrAll()
        );

        TrackUtils.executeStageIfNeeded(
                track,
                trackInfos,
                ( trackRecord ) -> TrackUtils.executeTrackActionFor(
                        user,
                        storage,
                        track.getIdentifier(),
                        trackRecord.getAction()
                )
        );
        // TODO: send executed message to the user and broadcast to all staff!
    }
}