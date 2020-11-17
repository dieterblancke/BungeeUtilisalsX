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

package com.dbsoftwares.bungeeutilisalsx.common.manager;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.Command;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import com.dbsoftwares.bungeeutilisalsx.common.commands.CustomCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.*;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.MsgToggleCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.MsgCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.IgnoreCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.ReplyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.spy.CommandSpyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.spy.SocialSpyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.plugin.PluginCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.report.ReportCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.friends.FriendsCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class CommandManager
{

    protected final List<Command> commands = Lists.newArrayList();

    public void load()
    {
        if ( !commands.isEmpty() )
        {
            this.unregisterAll();
        }

        // TODO
        this.registerCustomCommands();
    }

    protected void registerGeneralCommands()
    {
        registerGeneralCommand( "bungeeutilisals", new PluginCommandCall() );
        registerGeneralCommand( "server", new ServerCommandCall() );
        registerGeneralCommand( "announce", new AnnounceCommandCall() );
        registerGeneralCommand( "find", new FindCommandCall() );
        registerGeneralCommand( "glag", new GLagCommandCall() );
        registerGeneralCommand( "clearchat", new ClearChatCommandCall() );
        registerGeneralCommand( "helpop", new HelpOpCommandCall() );
        registerGeneralCommand( "staff", new StaffCommandCall() );
        registerGeneralCommand( "ping", new PingCommandCall() );
        registerGeneralCommand( "language", new LanguageCommandCall() );
        registerGeneralCommand( "glist", new GListCommandCall() );
        registerGeneralCommand( "shout", new ShoutCommandCall() );
        registerGeneralCommand( "socialspy", new SocialSpyCommandCall() );
        registerGeneralCommand( "commandspy", new CommandSpyCommandCall() );
        registerGeneralCommand( "msg", new MsgCommandCall() );
        registerGeneralCommand( "reply", new ReplyCommandCall() );
        registerGeneralCommand( "ignore", new IgnoreCommandCall() );
        registerGeneralCommand( "msgtoggle", new MsgToggleCommandCall() );
        registerGeneralCommand( "report", new ReportCommandCall() );

        if ( ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean( "server.slash-server.enabled" ) )
        {
            registerSlashServerCommands();
        }
        registerCommand(
                "friends",
                ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "enabled" ),
                ConfigFiles.FRIENDS_CONFIG.getConfig().getSection( "command" ),
                new FriendsCommandCall()
        );
    }

    protected void registerSlashServerCommands()
    {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.slash-server.permission" );

        for ( IProxyServer proxyServer : BuX.getInstance().proxyOperations().getServers() )
        {
            final String name = proxyServer.getName().toLowerCase();
            final CommandBuilder builder = CommandBuilder.builder()
                    .enabled( true )
                    .name( name )
                    .permission( permission.replace( "{server}", name ) )
                    .executable( new SlashServerCommandCall( name ) );

            buildCommand( name, builder );
        }
    }

    protected void registerCustomCommands()
    {
        final IConfiguration config = ConfigFiles.CUSTOMCOMMANDS.getConfig();

        for ( ISection section : config.getSectionList( "commands" ) )
        {
            final String name = section.getString( "name" );
            final List<String> aliases = section.exists( "aliases" ) ? section.getStringList( "aliases" ) : Lists.newArrayList();
            final String permission = section.exists( "permission" ) ? section.getString( "permission" ) : null;
            final List<String> commands = section.exists( "execute" ) ? section.getStringList( "execute" ) : Lists.newArrayList();
            final String server = section.exists( "server" ) ? section.getString( "server" ) : "ALL";

            final CommandBuilder commandBuilder = CommandBuilder.builder()
                    .enabled( true )
                    .name( name )
                    .aliases( aliases )
                    .permission( permission )
                    .executable( new CustomCommandCall( section, server, commands ) );

            buildCommand( name, commandBuilder );
        }
    }

    public void registerGeneralCommand( final String section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( section )
                .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( section ) )
                .executable( call );

        buildCommand( section, commandBuilder );
    }

    protected void registerPunishmentCommand( final String name, final String section, final CommandCall call, final List<String> parameters )
    {
        final IConfiguration config = ConfigFiles.PUNISHMENTS.getConfig();

        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( config.getSection( section ) )
                .executable( call );

        if ( parameters != null )
        {
            commandBuilder.parameters( parameters );
        }

        buildCommand( name, commandBuilder );
    }

    protected void registerCommand( final String name, final ISection section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( section )
                .executable( call );

        buildCommand( name, commandBuilder );
    }

    protected void registerCommand( final String name, final boolean enabled, final ISection section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( section )
                .enabled( enabled )
                .executable( call );

        buildCommand( name, commandBuilder );
    }

    protected void buildCommand( final String name, final CommandBuilder builder )
    {
        final Command command = builder.build();

        if ( command != null )
        {
            command.register();

            commands.add( command );
            log.info( "Registered a command named " + command.getName() + "." );
        }
        else
        {
            log.info( "Skipping registration of a command named " + name + "." );
        }
    }

    public void unregisterAll()
    {
        for ( Command command : commands )
        {
            command.unload();
        }
        commands.clear();
    }
}
