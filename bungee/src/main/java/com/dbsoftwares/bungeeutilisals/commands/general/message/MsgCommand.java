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

package com.dbsoftwares.bungeeutilisals.commands.general.message;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import java.util.Arrays;
import java.util.List;

public class MsgCommand extends BUCommand {

    public MsgCommand() {
        super(
                "msg",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("msg.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("msg.permission")
        );
    }

    @Override
    public void onExecute(User user, String[] args) {
        final String name = args[0];

        // TODO: base on friends msg? (make sure to not send to ignored players)
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }
}
