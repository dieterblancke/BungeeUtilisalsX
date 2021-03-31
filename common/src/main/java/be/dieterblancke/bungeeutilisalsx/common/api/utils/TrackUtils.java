package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;

import java.util.ArrayList;
import java.util.List;
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

    public static void executeStageIfNeeded( final PunishmentTrack track, final List<PunishmentTrackInfo> trackInfos, final Consumer<PunishmentTrackRecord> recordConsumer )
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
}
