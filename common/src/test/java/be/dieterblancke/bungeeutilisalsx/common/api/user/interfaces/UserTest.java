package be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces;

import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserTest
{

    @Test
    void testBuildLangMessage()
    {
        User user = mock( User.class, CALLS_REAL_METHODS );
        final IConfiguration languageConfig = IConfiguration.loadYamlConfiguration(
                this.getClass().getResourceAsStream( "/languages/en_US.yml" )
        );
        languageConfig.set( "general-commands.bungeeutilisals.help", Arrays.asList(
                "{#31BF3A}&lLOBBY{/#03FD13}",
                "{#F1A50C}&lSURVIVAL{/#FFE71E}",
                "{#2EA3BD}&lSURVIVAL{/#0CF1EE}"
        ) );
        languageConfig.set( "prefix", "[test] " );
        when( user.getLanguageConfig() ).thenReturn( languageConfig );

        doNothing().when( user ).sendMessage( any( BaseComponent.class ) );
        doNothing().when( user ).sendMessage( any( BaseComponent[].class ) );

        user.sendLangMessage( "general-commands.bungeeutilisals.help" );

        final ArgumentCaptor<BaseComponent[]> argumentCaptor = ArgumentCaptor.forClass( BaseComponent[].class );
        verify( user ).sendMessage( argumentCaptor.capture() );

        final String component = TextComponent.toLegacyText( argumentCaptor.getValue() );
        Assertions.assertEquals(
                "§f[test] §x§3§1§b§f§3§a§lL§x§2§6§c§e§3§1§lO§x§1§b§d§d§2§8§lB§x§1§0§e§c§1§f§lB§x§0§5§f§b§1§6§lY\n" +
                        "§x§f§1§a§5§0§c§lS§x§f§3§a§e§0§e§lU§x§f§5§b§7§1§0§lR§x§f§7§c§0§1§2§lV§x§f§9§c§9§1§4§lI§x§f§b§d§2§1§6§lV§x§f§d§d§b§1§8§lA§x§f§f§e§4§1§a§lL\n" +
                        "§x§2§e§a§3§b§d§lS§x§2§a§a§e§c§4§lU§x§2§6§b§9§c§b§lR§x§2§2§c§4§d§2§lV§x§1§e§c§f§d§9§lI§x§1§a§d§a§e§0§lV§x§1§6§e§5§e§7§lA§x§1§2§f§0§e§e§lL",
                component
        );
    }
}