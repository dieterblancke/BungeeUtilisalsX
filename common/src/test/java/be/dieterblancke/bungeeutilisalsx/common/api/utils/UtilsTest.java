package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest
{

    @Test
    void testGradientsColoring()
    {
        Assertions.assertEquals(
                "§x§0§0§0§0§0§0t§x§1§6§1§6§1§6e§x§2§c§2§c§2§cs§x§4§2§4§2§4§2t",
                Utils.c( "{#000000}test{/#444444}" )
        );
    }

    @Test
    void testHexColoring()
    {
        Assertions.assertEquals(
                "§x§1§1§1§1§1§1test",
                Utils.c( "<#111111>test" )
        );
    }

    @Test
    void testNormalColoring()
    {
        Assertions.assertEquals(
                "§1test",
                Utils.c( "&1test" )
        );
    }

    @Test
    void testParseDateDiff()
    {
        Assertions.assertEquals(
                Utils.parseDateDiff( "5d3h18m5s" ),
                System.currentTimeMillis()
                        + TimeUnit.DAYS.toMillis( 5 )
                        + TimeUnit.HOURS.toMillis( 3 )
                        + TimeUnit.MINUTES.toMillis( 18 )
                        + TimeUnit.SECONDS.toMillis( 5 )
        );
    }

    @Test
    void testParseDateDiffInPast()
    {
        Assertions.assertEquals(
                Utils.parseDateDiffInPast( "5d3h18m5s" ),
                System.currentTimeMillis()
                        - TimeUnit.DAYS.toMillis( 5 )
                        - TimeUnit.HOURS.toMillis( 3 )
                        - TimeUnit.MINUTES.toMillis( 18 )
                        - TimeUnit.SECONDS.toMillis( 5 )
        );
    }
}