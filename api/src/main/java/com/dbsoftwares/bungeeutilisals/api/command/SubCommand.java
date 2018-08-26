package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public abstract class SubCommand {

    private String name;
    private int espectedArgs;
    private int maximumArgs;

    public SubCommand(String name, int espectedArgs) {
        this(name, espectedArgs, espectedArgs);
    }

    public SubCommand(String name, int espectedArgs, int maximumArgs) {
        this.name = name;
        this.espectedArgs = espectedArgs;
        this.maximumArgs = maximumArgs;
    }

    public abstract String getUsage();

    public abstract String getPermission();

    public abstract void onExecute(User user, String[] args);

    private ConditionResult checkConditions(User user, String[] args) {
        if (!args[0].equalsIgnoreCase(name)) {
            return ConditionResult.FAILURE_WRONG_NAME;
        }
        if ((args.length - 1) != espectedArgs) {
            if ((args.length - 1) > maximumArgs) {
                return ConditionResult.FAILURE_WRONG_ARGS_LENGTH;
            }
        }
        if (!getPermission().isEmpty() && !user.sender().hasPermission(getPermission())) {
            return ConditionResult.FAILURE_PERMISSION;
        }
        return ConditionResult.SUCCESS;
    }

    public boolean execute(User user, String[] args) {
        ConditionResult result = checkConditions(user, args);

        if (result == ConditionResult.FAILURE_WRONG_ARGS_LENGTH) {
            user.sendLangMessage("subcommands.usage", "%usage%", getUsage());
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