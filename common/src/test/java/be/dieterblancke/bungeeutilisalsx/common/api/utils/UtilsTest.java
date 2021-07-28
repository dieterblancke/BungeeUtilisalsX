package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest
{

    @Test
    void testGradientsColoring()
    {
        assertEquals(
                "§x§0§0§0§0§0§0t§x§1§6§1§6§1§6e§x§2§c§2§c§2§cs§x§4§2§4§2§4§2t",
                Utils.c( "{#000000}test{/#444444}" )
        );
    }

    @Test
    void testHexColoring()
    {
        assertEquals(
                "§x§1§1§1§1§1§1test",
                Utils.c( "<#111111>test" )
        );
    }

    @Test
    void testNormalColoring()
    {
        assertEquals(
                "§1test",
                Utils.c( "&1test" )
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