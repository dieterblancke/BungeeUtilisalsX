package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.VersionsConfig.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.json.JsonConfiguration;
import be.dieterblancke.configuration.json.JsonSection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VersionsConfigTest
{

    @Test
    @DisplayName( "Testing config setup" )
    void testSetup1()
    {
        VersionsConfig versionsConfig = createConfig( false );
        versionsConfig.setup();

        assertThat( versionsConfig.getVersions() ).containsExactly(
                new Version( "test1", 1 ),
                new Version( "test2", 2 )
        );
    }

    @Test
    @DisplayName( "Testing unknown version with version present in config file" )
    void testGetUnknownVersion1()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getUnknownVersion() ).isEqualTo( new Version( "Unknown", -1 ) );
    }

    @Test
    @DisplayName( "Testing unknown version without version present in config file" )
    void testGetUnknownVersion2()
    {
        VersionsConfig versionsConfig = createConfig( false );
        versionsConfig.setup();

        assertThat( versionsConfig.getUnknownVersion() ).isEqualTo( new Version( "Unknown", -1 ) );
    }

    @Test
    @DisplayName( "Testing getting of version present in config file" )
    void testGetVersion1()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getVersion( 1 ) ).isEqualTo( new Version( "test1", 1 ) );
    }

    @Test
    @DisplayName( "Testing getting of version not present in config file" )
    void testGetVersion2()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getVersion( 10 ) ).isEqualTo( new Version( "Unknown", -1 ) );
    }

    @Test
    @DisplayName( "Testing getting latest version" )
    void testGetLatestVersion1()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getLatestVersion() ).isEqualTo( new Version( "test2", 2 ) );
    }

    @Test
    @DisplayName( "Testing getting version by name that is present in config file" )
    void testGetVersionByName1()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getVersionByName( "test1" ) )
                .isPresent()
                .hasValue( new Version( "test1", 1 ) );
    }

    @Test
    @DisplayName( "Testing getting version by name that is not present in config file" )
    void testGetVersionByName2()
    {
        VersionsConfig versionsConfig = createConfig( true );
        versionsConfig.setup();

        assertThat( versionsConfig.getVersionByName( "test" ) ).isEmpty();
    }

    @SneakyThrows
    private VersionsConfig createConfig( boolean unknownVersion )
    {
        VersionsConfig versionsConfig = new VersionsConfig();
        IConfiguration configuration = mock( JsonConfiguration.class );

        JsonSection section1 = new JsonSection();
        section1.set( "versionName", "test1" );
        section1.set( "protocolVersion", 1 );

        JsonSection section2 = new JsonSection();
        section2.set( "versionName", "test2" );
        section2.set( "protocolVersion", 2 );

        if ( unknownVersion )
        {
            JsonSection section3 = new JsonSection();
            section3.set( "versionName", "Unknown" );
            section3.set( "protocolVersion", -1 );
        }

        when( configuration.getSectionList( eq( "versions" ) ) ).thenReturn( List.of( section1, section2 ) );

        ReflectionUtils.setValue( versionsConfig, Config.class, "config", configuration );

        return versionsConfig;
    }
}