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
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.friends.FriendsCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.general.domains.DomainsCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanIPCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteIPCommand;
import com.dbsoftwares.bungeeutilisals.commands.report.ReportCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

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
    }

    private void loadGeneralCommands()
    {
        registerGeneralCommand( "report", new ReportCommandCall() );
        registerGeneralCommand( "domains", new DomainsCommandCall() );

        registerPunishmentCommand(
                "friends",
                FileLocation.FRIENDS_CONFIG.getConfiguration().getBoolean( "enabled" ),
                FileLocation.FRIENDS_CONFIG.getConfiguration().getSection( "command" ),
                new FriendsCommandCall()
        );
    }

    private void loadPunishmentCommands()
    {
        final IConfiguration config = FileLocation.PUNISHMENTS.getConfiguration();

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

        registerPunishmentCommand( "ban", "commands.ban", new BanCommand(), parameters );
        registerPunishmentCommand( "ipban", "commands.ipban", new IPBanCommand(), parameters );
        registerPunishmentCommand( "tempban", "commands.tempban", new TempBanCommand(), parameters );
        registerPunishmentCommand( "iptempban", "commands.iptempban", new IPTempBanCommand(), parameters );
        registerPunishmentCommand( "mute", "commands.mute", new MuteCommand(), parameters );
        registerPunishmentCommand( "ipmute", "commands.ipmute", new IPMuteCommand(), parameters );
        registerPunishmentCommand( "tempmute", "commands.tempmute", new TempMuteCommand(), parameters );
        registerPunishmentCommand( "iptempmute", "commands.iptempmute", new IPTempMuteCommand(), parameters );
        registerPunishmentCommand( "kick", "commands.kick", new KickCommand(), parameters );
        registerPunishmentCommand( "warn", "commands.warn", new WarnCommand(), parameters );
        registerPunishmentCommand( "unban", "commands.unban", new UnbanCommand(), parameters );
        registerPunishmentCommand( "unbanip", "commands.unbanip", new UnbanIPCommand(), parameters );
        registerPunishmentCommand( "unmute", "commands.unmute", new UnmuteCommand(), parameters );
        registerPunishmentCommand( "unmuteip", "commands.unmuteip", new UnmuteIPCommand(), parameters );
    }

    private void registerGeneralCommand( final String section, final CommandCall call )
    {
        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( section )
                .fromSection( FileLocation.GENERALCOMMANDS.getConfiguration().getSection( section ) )
                .executable( call );

        buildCommand( section, commandBuilder );
    }

    private void registerPunishmentCommand( final String name, final String section, final CommandCall call, final List<String> parameters )
    {
        final IConfiguration config = FileLocation.PUNISHMENTS.getConfiguration();

        final CommandBuilder commandBuilder = CommandBuilder.builder()
                .name( name )
                .fromSection( config.getSection( section ) )
                .executable( call )
                .parameters( parameters );

        buildCommand( name, commandBuilder );
    }

    private void registerPunishmentCommand( final String name, final boolean enabled, final ISection section, final CommandCall call )
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
            BUCore.getLogger().debug( "Registered a command named " + command.getName() + "." );
        }
        else
        {
            BUCore.getLogger().debug( "Skipping registration of a command named " + name + "." );
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
