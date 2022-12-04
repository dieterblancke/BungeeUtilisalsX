package be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.rexcantor64.triton.Triton;
import com.rexcantor64.triton.api.config.FeatureSyntax;
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
        String languageName = Triton.get().getStorage().getLanguageFromIp( Utils.getIP( (InetSocketAddress) event.getConnection().getSocketAddress() ) ).getName();
        FeatureSyntax syntax = Triton.get().getConf().getMotdSyntax();

        Players players = event.getResponse().getPlayers();
        if ( players.getSample() != null )
        {
            List<PlayerInfo> newSample = new ArrayList<>();

            for ( PlayerInfo playerInfo : players.getSample() )
            {
                String translatedName = Triton.get().getLanguageParser().replaceLanguages( playerInfo.getName(), languageName, syntax );
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
            Protocol translatedVersion = new Protocol( Triton.get().getLanguageParser().parseString( languageName, syntax, version.getName() ), version.getProtocol() );
            event.getResponse().setVersion( translatedVersion );

            event.getResponse().setDescriptionComponent( componentArrayToSingle( Triton.get().getLanguageParser()
                    .parseComponent( languageName, syntax, event.getResponse().getDescriptionComponent() ) ) );
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
