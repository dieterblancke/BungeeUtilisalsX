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

package be.dieterblancke.bungeeutilisalsx.common.commands.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.party.sub.*;

import java.util.List;

public class PartyCommandCall extends ParentCommand implements CommandCall
{

    public PartyCommandCall()
    {
        super( user ->
                {
                    if ( ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "command.send-message" ) )
                    {
                        user.sendLangMessage( "party.help.message" );
                    }
                }
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "create" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "create" )
                        .executable( new PartyCreateSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "invite" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "invite" )
                        .executable( new PartyInviteSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "accept" )
                        .executable( new PartyAcceptSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "leave" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "leave" )
                        .executable( new PartyLeaveSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "chat" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "chat" )
                        .executable( new PartyChatSubCommandCall() )
                        .build()
        );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            if ( ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "command.open-gui" )
                    && BuX.getInstance().isProtocolizeEnabled() )
            {
                BuX.getInstance().getProtocolizeManager().getGuiManager().openGui( user, "party", new String[0] );
            }
        }

        super.onExecute( user, args, parameters );
    }

    @Override
    public String getDescription()
    {
        return "This command either sends a list of available party commands, or it opens a GUI.";
    }

    @Override
    public String getUsage()
    {
        return "/party";
    }
}
