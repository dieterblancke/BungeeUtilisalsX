package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.TPSRunnable;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GLagCommand extends Command {

    public GLagCommand() {
        super(
                "glag",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("glag.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("glag.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        Long uptime = ManagementFactory.getRuntimeMXBean().getStartTime();
        SimpleDateFormat df2 = new SimpleDateFormat("kk:mm dd-MM-yyyy");
        String date = df2.format(new Date(uptime));

        double TPS = TPSRunnable.getTPS();

        user.sendLangMessage("general-commands.glag",
                "{tps}", getColor(TPS) + String.valueOf(TPS),
                "{maxmemory}", (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB",
                "{freememory}", (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB",
                "{totalmemory}", (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB",
                "{onlinesince}", date
        );
    }

    private ChatColor getColor(double tps) {
        if (tps >= 18.0) {
            return ChatColor.GREEN;
        } else if (tps >= 14.0) {
            return ChatColor.YELLOW;
        } else if (tps >= 8.0) {
            return ChatColor.RED;
        } else {
            return ChatColor.DARK_RED;
        }
    }
}
