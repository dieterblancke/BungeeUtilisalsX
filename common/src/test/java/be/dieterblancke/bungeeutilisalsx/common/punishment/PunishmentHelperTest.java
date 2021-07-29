package be.dieterblancke.bungeeutilisalsx.common.punishment;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import com.dbsoftwares.configuration.api.IConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PunishmentHelperTest
{

    private static PunishmentInfo punishmentInfo;
    private final PunishmentHelper punishmentHelper = new PunishmentHelper();

    @BeforeAll
    static void setup() throws ParseException
    {
        final Date date = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" ).parse( "29-07-2021 08:48:48" );

        punishmentInfo = new PunishmentInfo(
                PunishmentType.BAN,
                "1",
                "testuser",
                "127.0.0.1",
                UUID.fromString( "e7e5216f-c419-43b8-a5e7-525386c56e9b" ),
                "testexec",
                "testserver",
                "test reason",
                date,
                date.getTime() + TimeUnit.DAYS.toMillis( 5 ),
                true,
                null,
                "abc"
        );
    }

    @Test
    void testGetPlaceHolders() throws NoSuchFieldException, IllegalAccessException, ParseException, InterruptedException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.PUNISHMENT_CONFIG,
                mock( IConfiguration.class )
        );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "date-format" ) ).thenReturn( "dd-MM-yyyy kk:mm:ss" );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "time-left-format" ) ).thenReturn( "%days%d" );
        Thread.sleep( 3000 );

        final List<String> placeHolders = punishmentHelper.getPlaceHolders( punishmentInfo );

        assertEquals( Arrays.asList(
                "{reason}", "test reason",
                "{date}", "29-07-2021 08:48:48",
                "{by}", "testexec",
                "{server}", "testserver",
                "{uuid}", "e7e5216f-c419-43b8-a5e7-525386c56e9b",
                "{ip}", "127.0.0.1",
                "{user}", "testuser",
                "{id}", "1",
                "{type}", "ban",
                "{expire}", "03-08-2021 08:48:48",
                "{timeLeft}", "4d",
                "{removedBy}", "Unknown",
                "{punishment_uid}", "abc"
        ), placeHolders );
    }

    @Test
    void testSetPlaceHolders() throws NoSuchFieldException, IllegalAccessException, ParseException, InterruptedException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.PUNISHMENT_CONFIG,
                mock( IConfiguration.class )
        );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "date-format" ) ).thenReturn( "dd-MM-yyyy kk:mm:ss" );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "time-left-format" ) ).thenReturn( "%days%d" );

        Thread.sleep( 3000 );

        final String text = punishmentHelper.setPlaceHolders(
                "{reason} {date} {by} {server} {uuid} {ip} {user} {id} {type} {expire} {timeLeft} {removedBy} {punishment_uid}",
                punishmentInfo
        );

        assertEquals(
                "test reason 29-07-2021 08:48:48 testexec testserver e7e5216f-c419-43b8-a5e7-525386c56e9b 127.0.0.1 testuser 1 ban 03-08-2021 08:48:48 4d Unknown abc",
                text
        );
    }
}