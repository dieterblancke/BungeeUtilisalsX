package dev.endoy.bungeeutilisalsx.common.commands.domains;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.BuXTest;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.util.TestInjectionUtil;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.yaml.YamlSection;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DomainsListSubCommandCallTest extends BuXTest
{

    public DomainsListSubCommandCallTest()
    {
        super( true );
    }

    @Test
    void testDomainsListCommand() throws NoSuchFieldException, IllegalAccessException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.GENERALCOMMANDS,
                mock( IConfiguration.class )
        );
        when( ConfigFiles.GENERALCOMMANDS.getConfig().getSectionList( "domains.mappings" ) )
                .thenReturn( Collections.singletonList( new YamlSection()
                {{
                    this.set( "regex", ".*.play.example.com" );
                    this.set( "domain", "play.example.com" );
                }} ) );
        when( BuX
                .getApi()
                .getStorageManager()
                .getDao()
                .getUserDao()
                .getJoinedHostList() ).thenReturn( CompletableFuture.completedFuture( new HashMap<>()
        {{
            put( "test.play.example.com", 25 );
            put( "test8.play.example.com", 15 );
            put( "test446.play.example.com", 10 );
            put( "play.example.com", 30 );
        }} ) );

        final User user = Mockito.mock( User.class );

        final DomainsListSubCommandCall commandCall = new DomainsListSubCommandCall();
        commandCall.onExecute(
                user,
                Lists.newArrayList(),
                Lists.newArrayList()
        );

        final ArgumentCaptor<MessagePlaceholders> placeholdersCaptor = ArgumentCaptor.forClass( MessagePlaceholders.class );

        verify( user, VerificationModeFactory.atLeast( 1 ) )
                .sendLangMessage( eq( "general-commands.domains.list.format" ), placeholdersCaptor.capture() );

        final MessagePlaceholders placeholders = placeholdersCaptor.getValue();
        assertThat( placeholders.asArray() ).containsAll( Arrays.asList(
                "domain", "play.example.com",
                "total", 80,
                "online", 0L
        ) );
    }
}