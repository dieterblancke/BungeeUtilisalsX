package com.dbsoftwares.bungeeutilisals.api;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import net.md_5.bungee.api.CommandSender;

public final class BUCore {

    private static BUAPI instance = null;

    private BUCore() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Returns an instance of the {@link BUAPI},
     *
     * @return The BungeeUtilisals API.
     */
    public static BUAPI getApi() {
        return instance;
    }

    private static void initAPI(BUAPI inst) {
        instance = inst;
    }

    public static void sendMessage(CommandSender sender, String message) {
        IConfiguration config = getApi().getLanguageManager().getLanguageConfiguration(getApi().getPlugin(), sender);

        sender.sendMessage(Utils.format(config.getString("prefix"), message));
    }
}