package dev.endoy.bungeeutilisalsx.common.punishment;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.util.TestInjectionUtil;
import dev.endoy.configuration.api.IConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PunishmentHelperTest
{

    private static PunishmentInfo punishmentInfo;
    private final PunishmentHelper punishmentHelper = new PunishmentHelper();

    @BeforeAll
    static void setup() throws ParseException
    {
        final Date date = new Date( System.currentTimeMillis() - 10000 );

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
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis( 4 ) + TimeUnit.HOURS.toMillis( 5 ),
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
        final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" );

        final List<Object> placeHolders = Arrays.stream( punishmentHelper.getPlaceHolders( punishmentInfo ).asArray() ).toList();

        Assertions.assertThat( placeHolders ).containsAll( Arrays.asList(
                "reason", "test reason",
                "date", dateFormat.format( punishmentInfo.getDate() ),
                "by", "testexec",
                "server", "testserver",
                "uuid", "e7e5216f-c419-43b8-a5e7-525386c56e9b",
                "ip", "127.0.0.1",
                "user", "testuser",
                "id", "1",
                "type", "ban",
                "expire", dateFormat.format( new Date( punishmentInfo.getExpireTime() ) ),
                "timeLeft", "4d",
                "removedBy", "Unknown",
                "punishment_uid", "abc"
        ) );
    }

    @Test
    void testSetPlaceHolders() throws NoSuchFieldException, IllegalAccessException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.PUNISHMENT_CONFIG,
                mock( IConfiguration.class )
        );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "date-format" ) ).thenReturn( "dd-MM-yyyy kk:mm:ss" );
        when( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "time-left-format" ) ).thenReturn( "%days%d" );
        final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" );

        final String text = punishmentHelper.setPlaceHolders(
                "{reason} {date} {by} {server} {uuid} {ip} {user} {id} {type} {expire} {timeLeft} {removedBy} {punishment_uid}",
                punishmentInfo
        );

        assertEquals(
                "test reason " + dateFormat.format( punishmentInfo.getDate() ) + " testexec testserver " +
                        "e7e5216f-c419-43b8-a5e7-525386c56e9b 127.0.0.1 testuser 1 ban " + dateFormat.format( new Date( punishmentInfo.getExpireTime() ) ) + " 4d Unknown abc",
                text
        );
    }
}