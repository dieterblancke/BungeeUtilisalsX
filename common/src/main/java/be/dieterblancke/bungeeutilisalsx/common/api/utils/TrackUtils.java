package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack.PunishmentTrackRecord;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class TrackUtils
{

    private TrackUtils()
    {
        // empty constructor
    }

    public static int getMaxRunsForTrack( final PunishmentTrack track )
    {
        final List<PunishmentTrackRecord> records = new ArrayList<>( track.getRecords() );

        records.sort( ( o1, o2 ) -> Integer.compare( o2.getCount(), o1.getCount() ) );

        return records.get( 0 ).getCount();
    }

    public static boolean isFinished( final PunishmentTrack track, final List<PunishmentTrackInfo> trackInfos )
    {
        if ( track.getRecords().isEmpty() )
        {
            return true;
        }

        return trackInfos.size() >= getMaxRunsForTrack( track );
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

    public static void executeTrackActionFor( final User user,
                                              final UserStorage userStorage,
                                              final String reason,
                                              final String trackAction )
    {
        final String replacedTrackAction = trackAction
                .replace( "{user}", userStorage.getUserName() )
                .replace( "{reason}", reason );
        final String[] args = replacedTrackAction.split( " " );
        final String commandName = args[0];
        final List<String> arguments = Lists.newArrayList( Arrays.copyOfRange( args, 1, args.length ) );

        BuX.getInstance().getCommandManager().findCommandByName( commandName )
                .ifPresent( command -> command.getCommand().onExecute( user, arguments, new ArrayList<>() ) );
    }
}
