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

package com.dbsoftwares.bungeeutilisals.commands.general.spy;

import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;

public class SocialSpyCommandCall implements CommandCall
{
    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
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
}
