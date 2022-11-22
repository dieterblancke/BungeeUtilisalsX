package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TrackUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.Date;
import java.util.List;

public class TrackPunishCommandCall extends PunishmentCommand
{

    public TrackPunishCommandCall()
    {
        super( "punishments.track", false );
    }

    @Override
    public void onPunishmentExecute( final User user,
                                     final List<String> args,
                                     final List<String> parameters,
                                     final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final PunishmentTrack track = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( reason );
        final UserStorage storage = punishmentArgs.getStorage();

        if ( track == null )
        {
            user.sendLangMessage( "punishments.track.track-not-found" );
            return;
        }

        dao().getPunishmentDao().getTracksDao().getTrackInfos(
                storage.getUuid(),
                track.getIdentifier(),
                punishmentArgs.getServerOrAll()
        ).thenAccept( trackInfos ->
        {
            if ( TrackUtils.isFinished( track, trackInfos ) )
            {
                user.sendLangMessage( "punishments.track.track-already-finished" );
                return;
            }
            final PunishmentTrackInfo trackInfo = new PunishmentTrackInfo(
                    storage.getUuid(),
                    track.getIdentifier(),
                    punishmentArgs.getServerOrAll(),
                    user.getName(),
                    new Date(),
                    true
            );
            trackInfos.add( trackInfo );
            dao().getPunishmentDao().getTracksDao().addToTrack( trackInfo );

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

            final MessagePlaceholders messagePlaceholders = MessagePlaceholders.create()
                    .append( "user", storage.getUserName() )
                    .append( "track", track.getIdentifier() )
                    .append( "trackCount", trackInfos.size() )
                    .append( "trackMax", TrackUtils.getMaxRunsForTrack( track ) )
                    .append( "executed_by", user.getName() );

            user.sendLangMessage( "punishments.track.executed", messagePlaceholders );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                            "punishments.track.broadcast",
                            messagePlaceholders
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                            "punishments.track.broadcast",
                            ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.trackpunish.broadcast" ),
                            messagePlaceholders
                    );
                }
            }

            if ( TrackUtils.isFinished( track, trackInfos ) && track.isCanRunAgain() )
            {
                dao().getPunishmentDao().getTracksDao().resetTrack(
                        storage.getUuid(), track.getIdentifier(), punishmentArgs.getServerOrAll()
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Executes a track punishment for a specific user. Track punishments can be very useful for laying out a set punishment path for certain rule breakings.";
    }

    @Override
    public String getUsage()
    {
        return "/trackpunish (user) (reason)";
    }
}