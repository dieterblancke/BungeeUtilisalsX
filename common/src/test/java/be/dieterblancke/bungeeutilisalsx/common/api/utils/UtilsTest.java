package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilsTest
{

    @Test
    void testGradientsColoring()
    {
        Assertions.assertEquals(
                "§x§1§1§1§1§1§1t§x§2§2§2§2§2§2e§x§3§3§3§3§3§3s§x§4§4§4§4§4§4t",
                Utils.c( "<$#000000>test</$#444444>" )
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