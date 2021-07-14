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
}