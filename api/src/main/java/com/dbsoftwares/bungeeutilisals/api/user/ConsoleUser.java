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

package com.dbsoftwares.bungeeutilisals.api.user;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettings;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.MessageQueue;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.CanReceiveMessages;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.bungeeutilisals.api.utils.other.QueuedMessage;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ConsoleUser implements User, CanReceiveMessages
{

    private static final String NOT_SUPPORTED = "Not supported yet.";
    private final UserStorage storage = new UserStorage();
    private final UserCooldowns cooldowns = new UserCooldowns();
    @Getter
    private final List<FriendData> friends = Lists.newArrayList();
    @Getter
    @Setter
    private boolean socialSpy;
    @Getter
    @Setter
    private boolean commandSpy;

    @Override
    public void load( ProxiedPlayer parent )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void unload()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void save()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public UserStorage getStorage()
    {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns()
    {
        return cooldowns;
    }

    @Override
    public String getIp()
    {
        return "127.0.0.1";
    }

    @Override
    public Language getLanguage()
    {
        return BUCore.getApi().getLanguageManager().getDefaultLanguage();
    }

    @Override
    public void setLanguage( Language language )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public CommandSender sender()
    {
        return ProxyServer.getInstance().getConsole();
    }

    @Override
    public void sendRawMessage( String message )
    {
        sendMessage( TextComponent.fromLegacyText( message ) );
    }

    @Override
    public void sendRawColorMessage( String message )
    {
        if ( message.isEmpty() )
        {
            return;
        }
        sendMessage( Utils.format( message ) );
    }

    @Override
    public void sendMessage( String message )
    {
        if ( message.isEmpty() )
        {
            return;
        }
        sendMessage( getLanguageConfig().getString( "prefix" ), message );
    }

    @Override
    public void sendLangMessage( String path )
    {
        sendLangMessage( true, path );
    }

    @Override
    public void sendLangMessage( String path, Object... placeholders )
    {
        sendLangMessage( true, path, placeholders );
    }

    @Override
    public void sendLangMessage( boolean prefix, String path )
    {
        sendLangMessage( prefix, path, new Object[0] );
    }

    @Override
    public void sendLangMessage( boolean prefix, String path, Object... placeholders )
    {
        this.sendLangMessage( path, prefix, null, null, placeholders );
    }

    @Override
    public void sendLangMessage( final String path,
                                 boolean prefix,
                                 final Function<String, String> prePlaceholderFormatter,
                                 final Function<String, String> postPlaceholderFormatter,
                                 final Object... placeholders )
    {
        if ( getLanguageConfig().isSection( path ) )
        {
            // section detected, assuming this is a message to be handled by MessageBuilder (hover / focus events)
            final TextComponent component = MessageBuilder.buildMessage(
                    this, getLanguageConfig().getSection( path ), prePlaceholderFormatter, postPlaceholderFormatter, placeholders
            );

            sendMessage( component );
            return;
        }

        String message = buildLangMessage( path, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );

        if ( message.isEmpty() )
        {
            return;
        }

        if ( message.startsWith( "noprefix: " ) )
        {
            prefix = false;
            message = message.replaceFirst( "noprefix: ", "" );
        }

        if ( prefix )
        {
            sendMessage( message );
        }
        else
        {
            sendRawColorMessage( message );
        }
    }

    @Override
    public void sendMessage( String prefix, String message )
    {
        sendMessage( Utils.format( prefix + message ) );
    }

    @Override
    public void sendMessage( BaseComponent component )
    {
        if ( this.isEmpty( component ) )
        {
            return;
        }
        sender().sendMessage( component );
    }

    @Override
    public void sendMessage( BaseComponent[] components )
    {
        if ( this.isEmpty( components ) )
        {
            return;
        }
        sender().sendMessage( components );
    }

    @Override
    public void kick( String reason )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void langKick( String path, Object... placeholders )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void forceKick( String reason )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public String getName()
    {
        return "CONSOLE";
    }

    @Override
    public UUID getUuid()
    {
        return UUID.randomUUID();
    }

    @Override
    public void sendNoPermMessage()
    {
        sendLangMessage( "no-permission" );
    }

    @Override
    public ProxiedPlayer getParent()
    {
        sendLangMessage( "not-for-console" );
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public int getPing()
    {
        return 0;
    }

    @Override
    public IConfiguration getLanguageConfig()
    {
        return BUCore.getApi().getLanguageManager().getConfig( BUCore.getApi().getPlugin().getDescription().getName(), getLanguage() );
    }

    @Override
    public boolean isConsole()
    {
        return true;
    }

    @Override
    public String getServerName()
    {
        return "BUNGEE";
    }

    @Override
    public boolean isInStaffChat()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void setInStaffChat( boolean staffchat )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public Version getVersion()
    {
        return Version.latest();
    }

    @Override
    public Location getLocation()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void setLocation( Location location )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public String buildLangMessage( final String path, final Object... placeholders )
    {
        return this.buildLangMessage( path, null, null, placeholders );
    }

    @Override
    public String buildLangMessage(
            final String path,
            final Function<String, String> prePlaceholderFormatter,
            final Function<String, String> postPlaceholderFormatter,
            final Object... placeholders )
    {
        if ( !getLanguageConfig().exists( path ) )
        {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        if ( getLanguageConfig().isList( path ) )
        {
            final List<String> messages = getLanguageConfig().getStringList( path );

            if ( messages.isEmpty() )
            {
                return "";
            }

            for ( int i = 0; i < messages.size(); i++ )
            {
                final String message = replacePlaceHolders(
                        messages.get( i ),
                        prePlaceholderFormatter,
                        postPlaceholderFormatter,
                        placeholders
                );
                builder.append( message );

                if ( i < messages.size() - 1 )
                {
                    builder.append( "\n" );
                }
            }
        }
        else
        {
            final String message = replacePlaceHolders(
                    getLanguageConfig().getString( path ),
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );

            if ( message.isEmpty() )
            {
                return "";
            }

            builder.append( message );
        }
        return builder.toString();
    }

    @Override
    public FriendSettings getFriendSettings()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public boolean hasPermission( String permission )
    {
        return sender().hasPermission( permission );
    }

    @Override
    public MessageQueue<QueuedMessage> getMessageQueue()
    {
        return null;
    }

    @Override
    public void executeMessageQueue()
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }

    @Override
    public void sendPacket( DefinedPacket packet )
    {
        throw new UnsupportedOperationException( NOT_SUPPORTED );
    }
}