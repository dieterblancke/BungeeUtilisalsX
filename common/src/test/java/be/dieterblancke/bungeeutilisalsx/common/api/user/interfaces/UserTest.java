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
                this.getClass().getResourceAsStream( "/lnguages/en_US.yml" )
        );
        languageConfig.set( "general-commands.bungeeutilisals.help", Arrays.asList(
                "<$#31BF3A>&lLOBBY</$#03FD13>",
                "<$#F1A50C>&lSURVIVAL</$#FFE71E>",
                "<$#2EA3BD>&lSURVIVAL</$#0CF1EE>"
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
                "§f[test] §x§2§a§c§8§3§4§lL§x§2§3§d§1§2§e§lO§x§1§c§d§a§2§8§lB§x§1§5§e§3§2§2§lB§x§0§e§e§c§1§c§lY\n" +
                        "§x§f§2§a§c§0§e§lS§x§f§3§b§3§1§0§lU§x§f§4§b§a§1§2§lR§x§f§5§c§1§1§4§lV§x§f§6§c§8§1§6§lI§x§f§7§c§f§1§8§lV§x§f§8§d§6§1§a§lA§x§f§9§d§d§1§c§lL\n" +
                        "§x§2§b§a§b§c§2§lS§x§2§8§b§3§c§7§lU§x§2§5§b§b§c§c§lR§x§2§2§c§3§d§1§lV§x§1§f§c§b§d§6§lI§x§1§c§d§3§d§b§lV§x§1§9§d§b§e§0§lA§x§1§6§e§3§e§5§lL",
                component
        );
    }
}