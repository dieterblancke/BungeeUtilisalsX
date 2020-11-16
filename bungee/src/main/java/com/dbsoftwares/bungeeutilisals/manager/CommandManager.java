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

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.commands.friends.FriendsCommandCall;
import com.dbsoftwares.bungeeutilisalsx.bungee.commands.general.domains.DomainsCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.IgnoreCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.MsgCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.MsgToggleCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.message.ReplyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.spy.CommandSpyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.general.spy.SocialSpyCommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.commands.plugin.PluginCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanIPCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteIPCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.report.ReportCommandCall;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.commands.HubCommandCall;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.List;

public class CommandManager
{

    private final List<Command> commands = Lists.newArrayList();

    public void load()
    {
        loadGeneralCommands();
        loadPunishmentCommands();
    }

    private void loadGeneralCommands()
    {
        registerGeneralCommand( "report", new ReportCommandCall() );

        registerCommand(
                "friends",
                ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "enabled" ),
                ConfigFiles.FRIENDS_CONFIG.getConfig().getSection( "command" ),
                new FriendsCommandCall()
        );
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
}
