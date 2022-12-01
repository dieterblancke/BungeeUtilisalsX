package be.dieterblancke.bungeeutilisalsx.common.api.user;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCooldownsTest
{

    @SneakyThrows
    @Test
    @DisplayName( "Test user cooldowns" )
    void test1()
    {
        UserCooldowns userCooldowns = new UserCooldowns();
        assertTrue( userCooldowns.canUse( "test" ) );

        userCooldowns.updateTime( "test", TimeUnit.SECONDS, 2 );
        assertFalse( userCooldowns.canUse( "test" ) );

        Thread.sleep( 2000 );
        assertTrue( userCooldowns.canUse( "test" ) );
    }
}