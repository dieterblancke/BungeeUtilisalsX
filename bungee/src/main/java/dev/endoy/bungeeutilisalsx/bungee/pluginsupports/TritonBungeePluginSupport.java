package dev.endoy.bungeeutilisalsx.bungee.pluginsupports;

import com.rexcantor64.triton.api.Triton;
import com.rexcantor64.triton.api.TritonAPI;
import com.rexcantor64.triton.api.config.FeatureSyntax;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TritonBungeePluginSupport implements PluginSupport
{

    @Override
    public boolean isEnabled()
    {
        return BuX.getInstance().serverOperations().getPlugin( "Triton" ).isPresent();
    }

    @Override
    public void registerPluginSupport()
    {
    }

    public void handleMotd( ProxyPingEvent event )
    {
        Triton triton = TritonAPI.getInstance();
        String languageName = BuX.getApi().getStorageManager().getDao().getUserDao()
                .getUuidsOnIP( Utils.getIP( (InetSocketAddress) event.getConnection().getSocketAddress() ) )
                .join()
                .stream()
                .findFirst()
                .map( uuid -> triton.getPlayerManager().get( uuid ).getLang().getName() )
                .orElseGet( () -> triton.getLanguageManager().getMainLanguage().getName() );
        FeatureSyntax syntax = triton.getConf().getMotdSyntax();

        Players players = event.getResponse().getPlayers();
        if ( players.getSample() != null )
        {
            List<PlayerInfo> newSample = new ArrayList<>();

            for ( PlayerInfo playerInfo : players.getSample() )
            {
                String translatedName = triton.getLanguageParser().parseString( languageName, syntax, playerInfo.getName() );
                if ( playerInfo.getName() == null || playerInfo.getName().equals( translatedName ) )
                {
                    newSample.add( playerInfo );
                    continue;
                }
                if ( translatedName == null )
                {
                    continue;
                }
                String[] translatedNameSplit = translatedName.split( "\n", -1 );
                if ( translatedNameSplit.length > 1 )
                {
                    for ( String split : translatedNameSplit )
                    {
                        newSample.add( new PlayerInfo( split, UUID.randomUUID() ) );
                    }
                }
                else
                {
                    newSample.add( new PlayerInfo( translatedName, playerInfo.getUniqueId() ) );
                }
            }
            players.setSample( newSample.toArray( new PlayerInfo[0] ) );

            Protocol version = event.getResponse().getVersion();
            Protocol translatedVersion = new Protocol( triton.getLanguageParser().parseString( languageName, syntax, version.getName() ), version.getProtocol() );
            event.getResponse().setVersion( translatedVersion );
            event.getResponse().setDescriptionComponent( componentArrayToSingle( triton.getLanguageParser().parseComponent( languageName, syntax, event.getResponse().getDescriptionComponent() ) ) );
        }
    }

    private BaseComponent componentArrayToSingle( BaseComponent... c )
    {
        if ( c.length == 1 )
        {
            return c[0];
        }
        BaseComponent result = new TextComponent();
        result.setExtra( Arrays.asList( c ) );
        return result;
    }
}
