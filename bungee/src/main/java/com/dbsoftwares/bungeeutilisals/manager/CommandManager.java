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

package com.dbsoftwares.bungeeutilisals.manager;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.commands.CustomCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.friends.FriendsCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.*;
import com.dbsoftwares.bungeeutilisals.commands.general.domains.DomainsCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.message.IgnoreCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.message.MsgCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.message.ReplyCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.spy.CommandSpyCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.spy.SocialSpyCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.plugin.PluginCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanIPCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteIPCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.report.ReportCommandCall;
import com.dbsoftwares.bungeeutilisals.hubbalancer.commands.HubCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class CommandManager
{

    private final List<Command> commands = Lists.newArrayList();

    public void load()
    {
        if ( !commands.isEmpty() )
        {
            unregisterAll();
        }
        loadGeneralCommands();
        loadPunishmentCommands();
        loadCustomCommands();
    }

    private void loadGeneralCommands()
    {
        registerGeneralCommand( "bungeeutilisals", new PluginCommandCall() );
        registerGeneralCommand( "socialspy", new SocialSpyCommandCall() );
        registerGeneralCommand( "commandspy", new CommandSpyCommandCall() );
        registerGeneralCommand( "report", new ReportCommandCall() );
        registerGeneralCommand( "domains", new DomainsCommandCall() );
        registerGeneralCommand( "helpop", new HelpOpCommandCall() );
        registerGeneralCommand( "server", new ServerCommandCall() );
        registerGeneralCommand( "staffchat", new StaffChatCommandCall() );
        registerGeneralCommand( "staff", new StaffCommandCall() );
        registerGeneralCommand( "find", new FindCommandCall() );
        registerGeneralCommand( "ping", new PingCommandCall() );
        registerGeneralCommand( "language", new LanguageCommandCall() );
        registerGeneralCommand( "glag", new GLagCommandCall() );
        registerGeneralCommand( "announce", new AnnounceCommandCall() );
        registerGeneralCommand( "clearchat", new ClearChatCommandCall() );
        registerGeneralCommand( "chatlock", new ChatLockCommandCall() );
        registerGeneralCommand( "glist", new GListCommandCall() );
        registerGeneralCommand( "msg", new MsgCommandCall() );
        registerGeneralCommand( "reply", new ReplyCommandCall() );
        registerGeneralCommand( "ignore", new IgnoreCommandCall() );

        registerCommand(
                "friends",
                ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "enabled" ),
                ConfigFiles.FRIENDS_CONFIG.getConfig().getSection( "command" ),
                new FriendsCommandCall()
        );

        if ( ConfigFiles.GENERALCOMMANDS.getConfig().getBoolean( "server.slash-server.enabled" ) )
        {
            registerSlashServerCommands();
        }

        if ( ConfigFiles.HUBBALANCER.isEnabled() )
        {
            registerCommand(
                    "hub",
                    ConfigFiles.HUBBALANCER.getConfig().getSection( "commands.hub" ),
                    new HubCommandCall()
            );
        }
    }

    private void loadPunishmentCommands()
    {
        final IConfiguration config = ConfigFiles.PUNISHMENTS.getConfig();

        if ( !config.getBoolean( "enabled" ) )
        {
            return;
        }
        final List<String> parameters = Lists.newArrayList();
        final ISection parameterSection = config.getSection( "parameters" );

        for ( String key : parameterSection.getKeys() )
        {
            if ( parameterSection.getBoolean( key ) )
            {
                parameters.add( "-" + key );
            }
        }

        registerPunishmentCommand( "ban", "commands.ban", new BanCommandCall(), parameters );
        registerPunishmentCommand( "ipban", "commands.ipban", new IPBanCommandCall(), parameters );
        registerPunishmentCommand( "tempban", "commands.tempban", new TempBanCommandCall(), parameters );
        registerPunishmentCommand( "iptempban", "commands.iptempban", new IPTempBanCommandCall(), parameters );
        registerPunishmentCommand( "mute", "commands.mute", new MuteCommandCall(), parameters );
        registerPunishmentCommand( "ipmute", "commands.ipmute", new IPMuteCommandCall(), parameters );
        registerPunishmentCommand( "tempmute", "commands.tempmute", new TempMuteCommandCall(), parameters );
        registerPunishmentCommand( "iptempmute", "commands.iptempmute", new IPTempMuteCommandCall(), parameters );
        registerPunishmentCommand( "kick", "commands.kick", new KickCommandCall(), parameters );
        registerPunishmentCommand( "warn", "commands.warn", new WarnCommandCall(), parameters );
        registerPunishmentCommand( "unban", "commands.unban", new UnbanCommandCall(), parameters );
        registerPunishmentCommand( "unbanip", "commands.unbanip", new UnbanIPCommandCall(), parameters );
        registerPunishmentCommand( "unmute", "commands.unmute", new UnmuteCommandCall(), parameters );
        registerPunishmentCommand( "unmuteip", "commands.unmuteip", new UnmuteIPCommandCall(), parameters );

        registerPunishmentCommand( "punishmentinfo", "commands.punishmentinfo", new PunishmentInfoCommandCall(), null );
        registerPunishmentCommand( "punishmenthistory", "commands.punishmenthistory", new PunishmentHistoryCommandCall(), null );
        registerPunishmentCommand( "punishmentdata", "commands.punishmentdata", new PunishmentDataCommandCall(), null );
        registerPunishmentCommand( "checkip", "commands.checkip", new CheckIpCommandCall(), null );

        registerPunishmentCommand( "staffhistory", "commands.staffhistory", new StaffHistoryCommandCall(), parameters );
    }

    private void registerSlashServerCommands()
    {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.slash-server.permission" );

        for ( ServerInfo info : ProxyServer.getInstance().getServers().values() )
        {
            final String name = info.getName().toLowerCase();
            final CommandBuilder builder = CommandBuilder.builder()
                    .enabled( true )   x
                    .name( name )
                    .permission( permission.replace( "{server}", name ) )
                    .executable( new SlashServerCommandCall( name ) );

            buildCommand( name, builder );
        }
    }

    private void loadCustomCommands()
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

    private void registerGeneralCommand( final String section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( section )
                .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( section ) )
                .executable( call );

        buildCommand( section, commandBuilder );
    }

    private void registerPunishmentCommand( final String name, final String section, final CommandCall call, final List<String> parameters )
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

    private void registerCommand( final String name, final ISection section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( section )
                .executable( call );

        buildCommand( name, commandBuilder );
    }

    private void registerCommand( final String name, final boolean enabled, final ISection section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( section )
                .enabled( enabled )
                .executable( call );

        buildCommand( name, commandBuilder );
    }

    private void buildCommand( final String name, final CommandBuilder builder )
    {
        final Command command = builder.build();

        if ( command != null )
        {
            command.register();

            commands.add( command );
            BUCore.getLogger().info( "Registered a command named " + command.getName() + "." );
        }
        else
        {
            BUCore.getLogger().info( "Skipping registration of a command named " + name + "." );
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
