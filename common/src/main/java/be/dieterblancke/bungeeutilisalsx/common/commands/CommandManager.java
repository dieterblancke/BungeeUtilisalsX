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

package be.dieterblancke.bungeeutilisalsx.common.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.commands.domains.DomainsCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.friends.FriendsCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.*;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.message.IgnoreCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.message.MsgCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.message.MsgToggleCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.message.ReplyCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.spy.CommandSpyCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.spy.SocialSpyCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.plugin.PluginCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.*;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.removal.UnbanCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.removal.UnbanIPCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.removal.UnmuteCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.punishments.removal.UnmuteIPCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.report.ReportCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class CommandManager
{

    protected final List<Command> commands = Lists.newArrayList();

    public void load()
    {
        if ( !commands.isEmpty() )
        {
            this.unregisterAll();
        }

        this.registerGeneralCommands();
        this.registerCustomCommands();
        this.registerPunishmentCommands();
    }

    protected void registerGeneralCommands()
    {
        registerGeneralCommand( "bungeeutilisals", new PluginCommandCall() );
        registerGeneralCommand( "server", new ServerCommandCall() );
        registerGeneralCommand( "announce", new AnnounceCommandCall(), Lists.newArrayList("-p", "-a", "-b", "-c", "-t") );
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
        registerGeneralCommand( "chatlock", new ChatLockCommandCall() );
        registerGeneralCommand( "staffchat", new StaffChatCommandCall() );
        registerGeneralCommand( "domains", new DomainsCommandCall() );
        registerGeneralCommand( "staffannouncement", new StaffAnnouncementCommandCall() );

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

    protected void registerPunishmentCommands()
    {
        final IConfiguration config = ConfigFiles.PUNISHMENT_CONFIG.getConfig();

        if ( !config.getBoolean( "enabled" ) )
        {
            return;
        }
        final List<String> parameters = this.getParameterList( config.getSection( "parameters" ) );

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
        registerPunishmentCommand( "trackpunish", "commands.trackpunish", new TrackPunishCommandCall(), parameters );
        registerPunishmentCommand(
                "staffrollback",
                "commands.staffrollback",
                new StaffRollbackCommandCall(),
                this.getParameterList( config.getSection( "staffrollback-parameters" ) )
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
        this.registerGeneralCommand( section, call, new ArrayList<>() );
    }

    public void registerGeneralCommand( final String section, final CommandCall call, final List<String> parameters )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( section )
                .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( section ) )
                .parameters( parameters )
                .executable( call );

        buildCommand( section, commandBuilder );
    }

    protected void registerPunishmentCommand( final String name, final String section, final CommandCall call, final List<String> parameters )
    {
        final IConfiguration config = ConfigFiles.PUNISHMENT_CONFIG.getConfig();

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
            BuX.debug( "Registered a command named " + command.getName() + "." );
        }
        else
        {
            BuX.debug( "Skipping registration of a command named " + name + "." );
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

    public Optional<Command> findCommandByName( final String name )
    {
        return this.commands.stream()
                .filter( command ->
                        command.getName().equalsIgnoreCase( name )
                                || Arrays.stream( command.getAliases() ).anyMatch( alias -> alias.equalsIgnoreCase( name ) ) )
                .findFirst();
    }

    private List<String> getParameterList( final ISection parameterSection )
    {
        final List<String> parameters = Lists.newArrayList();

        for ( String key : parameterSection.getKeys() )
        {
            if ( parameterSection.getBoolean( key ) )
            {
                parameters.add( "-" + key );
            }
        }

        return parameters;
    }
}
