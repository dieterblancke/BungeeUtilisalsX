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
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.OpenGuiJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Strings;

import java.util.List;

public class OpenGuiCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.opengui.usage" );
            return;
        }
        final String gui = args.get( 0 );
        final String[] guiArgs = args.stream().skip( 1 ).toArray( String[]::new );

        if ( gui.equalsIgnoreCase( "custom" ) && guiArgs.length == 0 )
        {
            user.sendLangMessage( "general-commands.opengui.usage-custom" );
            return;
        }

        if ( parameters.stream().anyMatch( it -> it.startsWith( "-u" ) ) )
        {
            final String targetUser = parameters.stream()
                    .filter( it -> it.startsWith( "-u" ) )
                    .findFirst()
                    .map( it -> it.replaceFirst( "-u=", "" ) )
                    .orElse( "" );

            if ( !Strings.isNullOrEmpty( targetUser ) )
            {
                if ( !BuX.getApi().getPlayerUtils().isOnline( targetUser ) )
                {
                    user.sendLangMessage( "offline" );
                    return;
                }

                BuX.getInstance().getJobManager().executeJob( new OpenGuiJob( targetUser, gui, guiArgs ) );
                return;
            }
        }

        BuX.getInstance().getProtocolizeManager().getGuiManager().openGui(
                user,
                gui,
                guiArgs
        );
    }

    @Override
    public String getDescription()
    {
        return """
                Opens a given gui for yourself or another user, for another user, you need this permission: COMMANDPERMISSION.parameters.-u!
                For example: /opengui custom test will open a custom GUI named from the gui/custom/test.yml file.
                """;
    }

    @Override
    public String getUsage()
    {
        return "/opengui (gui) [args] [-u=USER]";
    }
}
