package com.dbsoftwares.bungeeutilisals.utils;

/*
 * Created by DBSoftwares on 24/08/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LanguageUtils {

    public static void sendLangMessage(final ProxiedPlayer player, final String path) {
        sendLangMessage((CommandSender) player, path);
    }

    public static void sendLangMessage(final ProxiedPlayer player, final String path, final Object... placeholders) {
        sendLangMessage((CommandSender) player, path, placeholders);
    }

    public static void sendLangMessage(final CommandSender sender, final String path) {
        final IConfiguration config = BUCore.getApi().getLanguageManager().getLanguageConfiguration(BungeeUtilisals.getInstance(), sender);
        if (config.isList(path)) {
            for (String message : config.getStringList(path)) {
                sender.sendMessage(Utils.format(message));
            }
        } else {
            sender.sendMessage(Utils.format(config.getString(path)));
        }
    }

    public static void sendLangMessage(final CommandSender sender, final String path, final Object... placeholders) {
        final IConfiguration config = BUCore.getApi().getLanguageManager().getLanguageConfiguration(BungeeUtilisals.getInstance(), sender);
        if (config.isList(path)) {
            for (String message : config.getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                sender.sendMessage(Utils.format(message));
            }
        } else {
            String message = config.getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            sender.sendMessage(Utils.format(message));
        }
    }
}
