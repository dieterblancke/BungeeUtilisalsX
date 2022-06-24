package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import be.dieterblancke.configuration.api.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrackUtilsTest
{

    private PunishmentTrack punishmentTrack;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.PUNISHMENT_TRACKS,
                IConfiguration.loadYamlConfiguration( this.getClass().getResourceAsStream( "/punishments/tracks.yml" ) )
        );
        ConfigFiles.PUNISHMENT_TRACKS.setup();
    }

    @Test
    void test_isFinished_with1Execution()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 1 );

        assertFalse( TrackUtils.isFinished( punishmentTrack, executedTracks ) );
    }

    @Test
    void test_isFinished_with2Executions()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 2 );

        assertFalse( TrackUtils.isFinished( punishmentTrack, executedTracks ) );
    }

    @Test
    void test_isFinished_with3Executions()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 3 );

        assertTrue( TrackUtils.isFinished( punishmentTrack, executedTracks ) );
    }

    @Test
    void test_executeStageIfNeeded_stageExecution_firstStage()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 1 );
        final AtomicBoolean hasRan = new AtomicBoolean( false );

        TrackUtils.executeStageIfNeeded( punishmentTrack, executedTracks, ( record ) -> hasRan.set( true ) );

        assertTrue( hasRan.get() );
    }

    @Test
    void test_executeStageIfNeeded_stageExecution_secondStage()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 2 );
        final AtomicBoolean hasRan = new AtomicBoolean( false );

        TrackUtils.executeStageIfNeeded( punishmentTrack, executedTracks, ( record ) -> hasRan.set( true ) );

        assertTrue( hasRan.get() );
    }

    @Test
    void test_executeStageIfNeeded_stageExecution_thirdStage()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 3 );
        final AtomicBoolean hasRan = new AtomicBoolean( false );

        TrackUtils.executeStageIfNeeded( punishmentTrack, executedTracks, ( record ) -> hasRan.set( true ) );

        assertTrue( hasRan.get() );
    }

    @Test
    void test_executeStageIfNeeded_stageExecution_noStage()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 0 );
        final AtomicBoolean hasRan = new AtomicBoolean( false );

        TrackUtils.executeStageIfNeeded( punishmentTrack, executedTracks, ( record ) -> hasRan.set( true ) );

        assertFalse( hasRan.get() );
    }

    @Test
    void test_executeStageIfNeeded_stageExecution_fourthStage_doesNotExist()
    {
        final UUID uuid = UUID.randomUUID();
        final PunishmentTrack punishmentTrack = ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTrack( "swearing" );
        final List<PunishmentTrackInfo> executedTracks = this.createPunishmentTrackInfos( uuid, punishmentTrack.getIdentifier(), 4 );
        final AtomicBoolean hasRan = new AtomicBoolean( false );

        TrackUtils.executeStageIfNeeded( punishmentTrack, executedTracks, ( record ) -> hasRan.set( true ) );

        assertFalse( hasRan.get() );
    }

    private List<PunishmentTrackInfo> createPunishmentTrackInfos( final UUID uuid, final String trackId, final int count )
    {
        final List<PunishmentTrackInfo> trackInfos = new ArrayList<>();

        for ( int i = 0; i < count; i++ )
        {
            trackInfos.add( this.createPunishmentTrackInfo( uuid, trackId ) );
        }

        return trackInfos;
    }

    private PunishmentTrackInfo createPunishmentTrackInfo( final UUID uuid, final String trackId )
    {
        return new PunishmentTrackInfo(
                uuid,
                trackId,
                "ALL",
                "CONSOLE",
                new Date(),
                true
        );
    }
}