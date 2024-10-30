package dev.endoy.bungeeutilisalsx.common.api.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest
{

    @Test
    void testParseDateDiff()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DAY_OF_MONTH, 5 );
        calendar.add( Calendar.HOUR, 3 );
        calendar.add( Calendar.MINUTE, 18 );
        calendar.add( Calendar.SECOND, 5 );

        assertEquals(
            Utils.parseDateDiff( "5d3h18m5s" ),
            calendar.getTimeInMillis(),
            50
        );
    }

    @Test
    void testParseDateDiffInPast()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DAY_OF_MONTH, -5 );
        calendar.add( Calendar.HOUR, -3 );
        calendar.add( Calendar.MINUTE, -18 );
        calendar.add( Calendar.SECOND, -5 );

        assertEquals(
            Utils.parseDateDiffInPast( "5d3h18m5s" ),
            calendar.getTimeInMillis(),
            50
        );
    }

    @Test
    @DisplayName( "Tests time left method with days, hours, minutes and seconds." )
    void testTimeLeft1()
    {
        assertEquals(
            "5d 8h 50m 38s",
            Utils.getTimeLeft(
                "%days%d %hours%h %minutes%m %seconds%s",
                TimeUnit.DAYS.toMillis( 5 ) + TimeUnit.HOURS.toMillis( 8 )
                    + TimeUnit.MINUTES.toMillis( 50 ) + TimeUnit.SECONDS.toMillis( 38 )
            )
        );
    }

    @Test
    @DisplayName( "Tests time left method with hours, minutes and seconds, no days." )
    void testTimeLeft2()
    {
        assertEquals(
            "0d 8h 50m 38s",
            Utils.getTimeLeft(
                "%days%d %hours%h %minutes%m %seconds%s",
                TimeUnit.HOURS.toMillis( 8 ) + TimeUnit.MINUTES.toMillis( 50 ) + TimeUnit.SECONDS.toMillis( 38 )
            )
        );
    }

    @Test
    @DisplayName( "Tests time left method with minutes and seconds, no days and hours." )
    void testTimeLeft3()
    {
        assertEquals(
            "0d 0h 50m 38s",
            Utils.getTimeLeft(
                "%days%d %hours%h %minutes%m %seconds%s",
                TimeUnit.MINUTES.toMillis( 50 ) + TimeUnit.SECONDS.toMillis( 38 )
            )
        );
    }

    @Test
    @DisplayName( "Tests time left method with seconds, no days, hours and minutes." )
    void testTimeLeft4()
    {
        assertEquals(
            "0d 0h 0m 38s",
            Utils.getTimeLeft(
                "%days%d %hours%h %minutes%m %seconds%s",
                TimeUnit.SECONDS.toMillis( 38 )
            )
        );
    }

    @Test
    @DisplayName( "Tests time left method with 0 timestamp." )
    void testTimeLeft5()
    {
        assertEquals(
            "0d 0h 0m 0s",
            Utils.getTimeLeft(
                "%days%d %hours%h %minutes%m %seconds%s",
                0
            )
        );
    }
}