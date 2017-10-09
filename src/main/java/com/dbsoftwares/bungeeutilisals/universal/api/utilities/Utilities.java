package com.dbsoftwares.bungeeutilisals.universal.api.utilities;

/*
 * Created by DBSoftwares on 03 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.universal.DBCommand;
import com.dbsoftwares.bungeeutilisals.universal.DBUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class Utilities {

    public static void loadCommand(DBCommand dbCommand) {
        Command command = new Command(dbCommand.getName(), "", dbCommand.getAliases()) {

            @Override
            public void execute(CommandSender sender, String[] args) {
                DBUser user = DBUser.searchUser(sender.getName());

                if (user == null) {
                    return;
                }

                dbCommand.execute(user, args);
            }
        };
        dbCommand.setCommand(command);
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeUtilisals.getInstance(), command);
    }

    public static void unloadCommand(DBCommand dbCommand) {
        ProxyServer.getInstance().getPluginManager().unregisterCommand((Command) dbCommand.getCommand());
    }

    public static String c(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static <K, V> K getKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}