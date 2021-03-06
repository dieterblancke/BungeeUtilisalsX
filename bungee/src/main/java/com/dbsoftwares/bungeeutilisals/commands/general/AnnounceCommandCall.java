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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.AnnounceMessage;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;

import java.util.List;
import java.util.Set;

public class AnnounceCommandCall implements CommandCall, TabCall
{

    public static void sendAnnounce( AnnounceMessage message )
    {
        for ( AnnouncementType type : message.getTypes() )
        {
            sendAnnounce( type, message.getMessage() );
        }
    }

    private static void sendAnnounce( AnnouncementType type, String message )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

        switch ( type )
        {
            case PRECONFIGURED:
                final String preconfiguredPath = config.getString( "announce.pre_configured" );

                for ( User user : BUCore.getApi().getUsers() )
                {
                    if ( !user.getLanguageConfig().exists( preconfiguredPath + "." + message ) )
                    {
                        continue;
                    }
                    final ISection section = user.getLanguageConfig().getSection( preconfiguredPath + "." + message );

                    if ( section.exists( "chat" ) )
                    {
                        if ( section.isSection( "chat" ) )
                        {
                            user.sendMessage(
                                    MessageBuilder.buildMessage(
                                            user, section.getSection( "chat" ),
                                            "{prefix}", config.getString( "announce.types.chat.prefix" )
                                    )
                            );
                        }
                        else
                        {
                            for ( String line : section.getString( "chat" ).split( "%nl%" ) )
                            {
                                user.sendRawColorMessage(
                                        config.getString( "announce.types.chat.prefix" ) + line.replace( "%sub%", "" )
                                );
                            }
                        }
                    }
                    if ( section.exists( "actionbar" ) )
                    {
                        sendActionBar( section.getString( "actionbar" ) );
                    }
                    if ( section.exists( "title" ) )
                    {
                        sendTitle( section.getSection( "title" ) );
                    }
                    if ( section.exists( "bossbar" ) )
                    {
                        sendBossBar( section.getString( "bossbar" ) );
                    }
                }
                break;
            case CHAT:
                if ( config.getBoolean( "announce.types.chat.enabled" ) )
                {
                    for ( String line : message.split( "%nl%" ) )
                    {
                        BUCore.getApi().announce(
                                config.getString( "announce.types.chat.prefix" ),
                                line.replace( "%sub%", "" )
                        );
                    }
                }
                break;
            case ACTIONBAR:
                sendActionBar( message );
                break;
            case TITLE:
                final String[] splitten = message.replace( "%nl%", "" ).split( "%sub%" );

                final String title = splitten[0];
                final String subtitle = splitten.length > 1 ? splitten[1] : "";
                sendTitle( title, subtitle );
                break;
            case BOSSBAR:
                sendBossBar( message );
                break;
        }
    }

    private static void sendBossBar( final String message )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();
        final List<IBossBar> bossBars = Lists.newArrayList();

        final BarColor color = BarColor.valueOf( config.getString( "announce.types.bossbar.color" ) );
        final BarStyle style = BarStyle.valueOf( config.getString( "announce.types.bossbar.style" ) );
        float progress = config.getFloat( "announce.types.bossbar.progress" );
        long stay = config.getInteger( "announce.types.bossbar.stay" );

        BUCore.getApi().getUsers().forEach( user ->
        {
            IBossBar bossBar = BUCore.getApi().createBossBar(
                    color, style, progress,
                    Utils.format( user, message.replace( "%sub%", "" ).replace( "%nl%", "" ) )
            );

            bossBar.addUser( user );
            bossBars.add( bossBar );
        } );

        ProxyServer.getInstance().getScheduler().schedule(
                BungeeUtilisals.getInstance(), () ->
                        bossBars.forEach( bossBar ->
                        {
                            bossBar.clearUsers();

                            bossBar.unregister();
                        } ),
                stay, TimeUnit.SECONDS.toJavaTimeUnit()
        );

    }

    private static void sendActionBar( final String message )
    {
        if ( ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean( "announce.types.actionbar.enabled" ) )
        {
            ProxyServer.getInstance().getPlayers().forEach( p -> p.sendMessage(
                    ChatMessageType.ACTION_BAR,
                    Utils.format( p, message.replace( "%nl%", "" ).replace( "%sub%", "" ) )
            ) );
        }
    }

    private static void sendTitle( final ISection section )
    {
        sendTitle( section.getString( "main" ), section.getString( "sub" ) );
    }

    private static void sendTitle( final String title, final String subtitle )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

        if ( config.getBoolean( "announce.types.title.enabled" ) )
        {
            final int fadein = config.getInteger( "announce.types.title.fadein" );
            final int stay = config.getInteger( "announce.types.title.stay" );
            final int fadeout = config.getInteger( "announce.types.title.fadeout" );

            ProxyServer.getInstance().getPlayers().forEach( p ->
            {
                Title bungeeTitle = ProxyServer.getInstance().createTitle();

                bungeeTitle.title( Utils.format( p, title ) );
                bungeeTitle.subTitle( Utils.format( p, subtitle ) );
                bungeeTitle.fadeIn( fadein * 20 );
                bungeeTitle.stay( stay * 20 );
                bungeeTitle.fadeOut( fadeout * 20 );

                p.sendTitle( bungeeTitle );
            } );
        }
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() >= 2 )
        {
            final String types = args.get( 0 );
            final String message = Joiner.on( " " ).join( args.subList( 1, args.size() ) );

            final AnnounceMessage announceMessage = new AnnounceMessage( getTypes( types ), message );
            sendAnnounce( announceMessage );
        }
        else
        {
            user.sendLangMessage( "general-commands.announce.usage" );
        }
    }

    private Set<AnnouncementType> getTypes( String types )
    {
        final Set<AnnouncementType> announcementTypes = Sets.newHashSet();

        if ( types.contains( "p" ) )
        {
            announcementTypes.add( AnnouncementType.PRECONFIGURED );
            return announcementTypes;
        }
        if ( types.contains( "a" ) )
        {
            announcementTypes.add( AnnouncementType.ACTIONBAR );
        }
        if ( types.contains( "c" ) )
        {
            announcementTypes.add( AnnouncementType.CHAT );
        }
        if ( types.contains( "t" ) )
        {
            announcementTypes.add( AnnouncementType.TITLE );
        }
        if ( types.contains( "b" ) )
        {
            announcementTypes.add( AnnouncementType.BOSSBAR );
        }

        return announcementTypes;
    }
}
