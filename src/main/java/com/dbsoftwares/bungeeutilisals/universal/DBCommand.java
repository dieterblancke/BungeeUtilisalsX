package com.dbsoftwares.bungeeutilisals.universal;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public abstract class DBCommand {

    @Getter @Setter Object command;
    private String name;
    private String[] aliases;
    protected List<String> permissions = Lists.newArrayList();

    public DBCommand(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
        permissions.add("bungeeutilisals." + name);
        permissions.add("bungeeutilisals.*");
    }

    public DBCommand(String name, List<String> aliases) {
        this(name, aliases.toArray(new String[]{}));
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void execute(DBUser user, String[] args) {
        if (user.isUser()) {
            for (String perm : permissions) {
                if (user.hasPermission(perm)) {
                    onExecute(user, args);
                    break;
                }
            }
        } else {
            onExecute(user, args);
        }
    }

    public abstract void onExecute(DBUser user, String[] args);
}