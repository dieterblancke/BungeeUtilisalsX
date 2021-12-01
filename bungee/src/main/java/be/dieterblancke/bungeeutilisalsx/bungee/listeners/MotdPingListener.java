/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeMotdConnection;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Version;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionHandler;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdData;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MotdPingListener implements Listener
{

    @EventHandler
    public void onPing( ProxyPingEvent event )
    {
        final List<MotdData> dataList = ConfigFiles.MOTD.getMotds();
        final MotdConnection motdConnection = this.createMotdConnection( event.getConnection() );

        ServerPing result = loadConditionalMotd( event, motdConnection, dataList );
        if ( result == null )
        {
            result = loadDefaultMotd( event, motdConnection, dataList );
        }

        if ( result != null )
        {
            event.setResponse( result );
        }
    }

    private ServerPing loadMotd( final ServerPing orig, final MotdConnection connection, final MotdData motd )
    {
        if ( motd == null )
        {
            return null;
        }
        final boolean colorHex = Version.getVersion( connection.getVersion() ).isNewerThen( Version.MINECRAFT_1_16 );
        final String message = formatMessage( motd.getMotd(), connection );
        final BaseComponent component = new TextComponent(
                TextComponent.fromLegacyText( Utils.formatString( message, colorHex ) )
        );

        final List<PlayerInfo> hoverMessages = Lists.newArrayList();
        for ( int i = 0; i < motd.getHoverMessages().size(); i++ )
        {
            final String hoverMessage = motd.getHoverMessages().get( i );

            hoverMessages.add( new PlayerInfo(
                    Utils.formatString( formatMessage( hoverMessage, connection ), colorHex ),
                    String.valueOf( i )
            ) );
        }
        final PlayerInfo[] hover = hoverMessages.toArray( new PlayerInfo[0] );

        orig.setPlayers( new Players( orig.getPlayers().getMax(), orig.getPlayers().getOnline(), hover ) );
        orig.setDescriptionComponent( component );

        return orig;
    }

    private String formatMessage( String message, final MotdConnection connection )
    {
        final Version version = Version.getVersion( connection.getVersion() );

        message = message.replace( "{user}", connection.getName() == null ? "Unknown" : connection.getName() );
        message = message.replace( "{version}", version == null ? "Unknown" : version.toString() );

        if ( connection.getVirtualHost() == null || connection.getVirtualHost().getHostName() == null )
        {
            message = message.replace( "{domain}", "Unknown" );
        }
        else
        {
            message = message.replace( "{domain}", connection.getVirtualHost().getHostName() );
        }

        return message;
    }

    private ServerPing loadDefaultMotd( final ProxyPingEvent event, final MotdConnection connection, final List<MotdData> motds )
    {
        final List<MotdData> defMotds = motds.stream().filter( MotdData::isDef ).collect( Collectors.toList() );
        final MotdData motd = MathUtils.getRandomFromList( defMotds );

        return loadMotd( event.getResponse(), connection, motd );
    }

    private ServerPing loadConditionalMotd( final ProxyPingEvent event, final MotdConnection connection, final List<MotdData> motds )
    {
        final List<MotdData> conditions = motds.stream().filter( data -> !data.isDef() ).collect( Collectors.toList() );

        for ( final MotdData condition : conditions )
        {
            final ConditionHandler handler = condition.getConditionHandler();

            if ( handler.checkCondition( connection ) )
            {
                final List<MotdData> conditionalMotds = conditions.stream().filter(
                        data -> data.getConditionHandler().getCondition().equalsIgnoreCase( handler.getCondition() )
                ).collect( Collectors.toList() );
                final MotdData motd = MathUtils.getRandomFromList( conditionalMotds );

                return loadMotd( event.getResponse(), connection, motd );
            }
        }
        return null;
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private MotdConnection createMotdConnection( final PendingConnection connection )
    {
        final String name = BuX.getApi().getStorageManager().getDao().getUserDao()
                .getUsersOnIP( Utils.getIP( (InetSocketAddress) connection.getSocketAddress() ) )
                .stream()
                .findFirst()
                .orElse( null );

        return new BungeeMotdConnection(
                connection.getVersion(),
                name,
                connection.getVirtualHost()
        );
    }
}