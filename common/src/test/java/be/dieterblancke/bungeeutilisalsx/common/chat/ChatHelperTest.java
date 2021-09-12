package be.dieterblancke.bungeeutilisalsx.common.chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChatHelperTest
{

    @Test
    @DisplayName( "Testing fancy font with color code in beginning of message" )
    void testChangeToFancyFont1()
    {
        final String fancified = ChatHelper.changeToFancyFont( "&6This is a test message" );

        Assertions.assertEquals( "&6Ｔｈｉｓ ｉｓ ａ ｔｅｓｔ ｍｅｓｓａｇｅ", fancified );
    }

    @Test
    @DisplayName( "Testing fancy font with color code in beginning and middle of message" )
    void testChangeToFancyFont2()
    {
        final String fancified = ChatHelper.changeToFancyFont( "&6This is a &etest message" );

        Assertions.assertEquals( "&6Ｔｈｉｓ ｉｓ ａ &eｔｅｓｔ ｍｅｓｓａｇｅ", fancified );
    }
}