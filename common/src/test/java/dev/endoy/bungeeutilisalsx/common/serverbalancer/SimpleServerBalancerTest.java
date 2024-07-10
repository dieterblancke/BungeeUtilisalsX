package dev.endoy.bungeeutilisalsx.common.serverbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleServerBalancerTest
{

    private SimpleServerBalancer serverBalancer;

    @BeforeEach
    void setup()
    {
        this.serverBalancer = new SimpleServerBalancer();
    }

    @Test
    @DisplayName( "Test setup" )
    void testSetup1()
    {
        assertThat( serverBalancer.isInitialized() ).isFalse();
        serverBalancer.setup();
        assertThat( serverBalancer.isInitialized() ).isTrue();
    }

    @Test
    @DisplayName( "Test setup when already initialized" )
    void testSetup2()
    {
        serverBalancer.setup();
        assertThrows( IllegalStateException.class, () -> serverBalancer.setup() );
    }

    @Test
    @DisplayName( "Test shutdown" )
    void testShutdown1()
    {
        serverBalancer.setup();
        serverBalancer.shutdown();
        assertThat( serverBalancer.isInitialized() ).isFalse();
    }

    @Test
    @DisplayName( "Test shutdown when not initialized" )
    void testShutdown2()
    {
        assertThrows( IllegalStateException.class, () -> serverBalancer.shutdown() );
    }
}