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

package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public abstract class SubCommand {

    private String name;
    private int minimumArgs;
    private int maximumArgs;

    public SubCommand(String name) {
        this(name, 0, 0);
    }

    public SubCommand(String name, int minimumArgs) {
        this(name, minimumArgs, minimumArgs);
    }

    public SubCommand(String name, int minimumArgs, int maximumArgs) {
        this.name = name;
        this.minimumArgs = minimumArgs;
        this.maximumArgs = maximumArgs;
    }

    public abstract String getUsage();

    public abstract String getPermission();

    public abstract void onExecute(User user, String[] args);

    private ConditionResult checkConditions(User user, String[] args) {
        if (!args[0].equalsIgnoreCase(name)) {
            return ConditionResult.FAILURE_WRONG_NAME;
        }
        final int length = args.length - 1;

        if (length < minimumArgs || length > maximumArgs) {
            return ConditionResult.FAILURE_WRONG_ARGS_LENGTH;
        }
        if (!getPermission().isEmpty()
                && !user.sender().hasPermission(getPermission())
                && !user.sender().hasPermission("bungeeutilisals.commands.*")
                && !user.sender().hasPermission("bungeeutilisals.*")
                && !user.sender().hasPermission("*")) {
            return ConditionResult.FAILURE_PERMISSION;
        }
        return ConditionResult.SUCCESS;
    }

    public boolean execute(User user, String[] args) {
        final ConditionResult result = checkConditions(user, args);

        if (result == ConditionResult.FAILURE_WRONG_ARGS_LENGTH) {
            user.sendLangMessage("subcommands.usage", "{usage}", getUsage());
            return true;
        } else if (result == ConditionResult.FAILURE_PERMISSION) {
            user.sendLangMessage("no-permission");
            return true;
        } else if (result == ConditionResult.SUCCESS) {
            onExecute(user, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        return false;
    }

    public abstract List<String> getCompletions(User user, String[] args);

    public enum ConditionResult {

        FAILURE_PERMISSION, FAILURE_WRONG_NAME, FAILURE_WRONG_ARGS_LENGTH, SUCCESS

    }
}