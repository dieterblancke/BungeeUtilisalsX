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

package be.dieterblancke.bungeeutilisalsx.common.api.event.events.user;

import be.dieterblancke.bungeeutilisalsx.common.api.event.AbstractEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Cancellable;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

/**
 * This event is being executed upon User Command execute.
 */
@Data
@EqualsAndHashCode( callSuper = true )
public class UserCommandEvent extends AbstractEvent implements Cancellable
{

    private User user;
    private String command;
    private boolean cancelled = false;

    public UserCommandEvent( final User user, final String command )
    {
        this.user = user;
        this.command = command;
    }

    public String getActualCommand()
    {
        return command.split( " " )[0].toLowerCase();
    }

    public String[] getArguments()
    {
        final String[] arguments = command.split( " " );

        return Arrays.copyOfRange( arguments, 1, arguments.length );
    }
}
