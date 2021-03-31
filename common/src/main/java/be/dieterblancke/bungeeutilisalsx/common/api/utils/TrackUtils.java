package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.TrackAction;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class TrackUtils
{

    private TrackUtils()
    {
        // empty constructor
    }

    public static boolean isFinished( final PunishmentTrack track, final List<PunishmentTrackInfo> trackInfos )
    {
        if ( track.getRecords().isEmpty() )
        {
            return true;
        }
        final int amountExecuted = trackInfos.size();
        final List<PunishmentTrackRecord> records = new ArrayList<>( track.getRecords() );

        records.sort( ( o1, o2 ) -> Integer.compare( o2.getCount(), o1.getCount() ) );

        final int maxRuns = records.get( 0 ).getCount();
        return amountExecuted >= maxRuns;
    }

    public static void executeStageIfNeeded( final PunishmentTrack track,
                                             final List<PunishmentTrackInfo> trackInfos,
                                             final Consumer<PunishmentTrackRecord> recordConsumer )
    {
        if ( track.getRecords().isEmpty() )
        {
            return;
        }
        final int amountExecuted = trackInfos.size();
        final List<PunishmentTrackRecord> records = new ArrayList<>( track.getRecords() );

        records.sort( ( o1, o2 ) -> Integer.compare( o2.getCount(), o1.getCount() ) );

        for ( PunishmentTrackRecord record : records )
        {
            if ( amountExecuted == record.getCount() )
            {
                recordConsumer.accept( record );
            }
        }
    }

    public static void executeTrackActionFor( final UUID uuid, final String executedBy, final TrackAction trackAction )
    {
        // TODO: execute punishment for the specific trackAction
    }

    public static void addTrackPunishment( final UUID uuid, final PunishmentInfo punishmentInfo )
    {
        // TODO: create test for this

        for ( PunishmentTrack punishmentTrack : ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTracks() )
        {
            if ( punishmentTrack.getCountNormalPunishments().isEnabled()
                    && punishmentTrack.getCountNormalPunishments().getTypes().contains( punishmentInfo.getType() ) )
            {
                // TODO: count this to track if the option is enabled (so call addToTrack)
            }
        }
    }
}
