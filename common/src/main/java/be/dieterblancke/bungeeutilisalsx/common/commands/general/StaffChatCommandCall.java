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

package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.base.Strings;

import java.util.List;

public class StaffChatCommandCall implements CommandCall
{

    public static void sendStaffChatMessage( final User user, final String message )
    {
        BuX.getInstance().getJobManager().executeJob(
                new BroadcastLanguageMessageJob(
                        "general-commands.staffchat.format",
                        ConfigFiles.GENERALCOMMANDS.getConfig().getString( "staffchat.permission" ),
                        "{user}", user.getName(),
                        "{user_prefix}", StaffUtils.getStaffRankForUser( user ).map( rank -> Utils.c( rank.getDisplay() ) ).orElse( "" ),
                        "{permission_user_prefix}", Strings.nullToEmpty( BuX.getInstance().getActivePermissionIntegration().getPrefix( user.getUuid() ) ),
                        "{permission_user_prefix}", Strings.nullToEmpty( BuX.getInstance().getActivePermissionIntegration().getSuffix( user.getUuid() ) ),
                        "{server}", Strings.nullToEmpty( user.getServerName() ),
                        "{message}", message
                )
        );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        // If amount of arguments > 0, then we should directly send a message in staff chat
        if ( args.size() > 0 )
        {
            sendStaffChatMessage( user, String.join( " ", args ) );
            return;
        }

        user.setInStaffChat( !user.isInStaffChat() );
        user.sendLangMessage( "general-commands.staffchat." + ( user.isInStaffChat() ? "enabled" : "disabled" ) );
    }

    @Override
    public String getDescription()
    {
        return "Toggles your chat mode into staff chat mode, only people with a given permission can then see your messages.";
    }

    @Override
    public String getUsage()
    {
        return "/staffchat [message]";
    }
}
