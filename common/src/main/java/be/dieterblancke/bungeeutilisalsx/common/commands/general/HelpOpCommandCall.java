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
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class HelpOpCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.helpop.usage" );
            return;
        }
        if ( args.get( 0 ).equalsIgnoreCase( "reply" ) && args.size() > 2 )
        {
            executeReplySubCommand( user, args );
            return;
        }
        final String message = String.join( " ", args );
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "helpop.receive-broadcast" );

        if ( !user.hasPermission( permission ) )
        {
            user.sendLangMessage(
                    "general-commands.helpop.broadcast",
                    "{message}", message,
                    "{user}", user.getName(),
                    "{user_server}", user.getServerName()
            );
        }

        BuX.getApi().langPermissionBroadcast(
                "general-commands.helpop.broadcast",
                permission,
                "{message}", message,
                "{user}", user.getName(),
                "{user_server}", user.getServerName()
        );
    }

    @Override
    public String getDescription()
    {
        return "Sends a helpop message to the online staff. Staff can reply using /helpop reply (user) (message).";
    }

    @Override
    public String getUsage()
    {
        return "/helpop [reply] (message)";
    }

    private void executeReplySubCommand( final User user, final List<String> args )
    {
        if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "helpop.reply-permission" ) ) )
        {
            user.sendLangMessage( "no-permission" );
            return;
        }

        final String targetName = args.get( 1 );
        final String message = String.join( " ", args.subList( 2, args.size() ) );

        if ( !BuX.getApi().getPlayerUtils().isOnline( targetName ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        user.sendLangMessage(
                "general-commands.helpop.reply-send",
                "{user}", targetName,
                "{message}", message
        );

        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                targetName,
                "general-commands.helpop.reply-receive",
                "{user}", user.getName(),
                "{message}", message
        ) );
    }
}
