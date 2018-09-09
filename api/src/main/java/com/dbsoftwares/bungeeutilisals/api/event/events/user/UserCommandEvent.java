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

package com.dbsoftwares.bungeeutilisals.api.event.events.user;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.event.Cancellable;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Getter;
import lombok.Setter;

/**
 * This event is being executed upon User Command execute.
 */
public class UserCommandEvent extends AbstractEvent implements Cancellable {

    @Getter @Setter User user;
    @Getter @Setter String command;
    @Getter
    @Setter
    private boolean cancelled = false;

    public UserCommandEvent(User user, String command) {
        this.user = user;
        this.command = command;
    }

    public String getActualCommand() {
        return command.split(" ")[0].toLowerCase();
    }
}
