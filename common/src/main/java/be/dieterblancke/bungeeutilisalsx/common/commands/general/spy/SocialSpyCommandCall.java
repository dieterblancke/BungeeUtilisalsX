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

package be.dieterblancke.bungeeutilisalsx.common.commands.general.spy;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class SocialSpyCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( !user.isSocialSpy() )
        {
            user.sendLangMessage( "general-commands.socialspy.enabled" );
            user.setSocialSpy( true );
        }
        else
        {
            user.sendLangMessage( "general-commands.socialspy.disabled" );
            user.setSocialSpy( false );
        }
    }

    @Override
    public String getDescription()
    {
        return "Toggles social spy for the current session. This will allow you to see all private messages that are being sent.";
    }

    @Override
    public String getUsage()
    {
        return "/socialspy";
    }
}
