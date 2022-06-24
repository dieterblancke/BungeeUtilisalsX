package be.dieterblancke.bungeeutilisalsx.common.commands.domains;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.BuXTest;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.yaml.YamlSection;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        final ArgumentCaptor<Object> placeholdersCaptor = ArgumentCaptor.forClass( Object.class );

        verify( user, VerificationModeFactory.atLeast( 1 ) )
                .sendLangMessage( eq( "general-commands.domains.list.format" ), placeholdersCaptor.capture() );

        final List<Object> placeholders = placeholdersCaptor.getAllValues();

        for ( int i = 0; i < placeholders.size(); i++ )
        {
            final String placeholder = String.valueOf( placeholders.get( i ) );

            if ( placeholder.equals( "{domain}" ) )
            {
                Assertions.assertEquals( placeholders.get( i + 1 ), "play.example.com" );
            }
            if ( placeholder.equals( "{total}" ) )
            {
                Assertions.assertEquals( placeholders.get( i + 1 ), 80 );
            }
        }
    }
}