package com.dbsoftwares.bungeeutilisals.api;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.Optional;

public final class BUCore {

    private static BUAPI instance = null;

    /**
     * Gets an instance of the {@link BUAPI},
     * throwing {@link IllegalStateException} if an instance is not yet loaded.
     *
     * <p>Will never return null.</p>
     *
     * @return an api instance
     * @throws IllegalStateException if the api is not loaded
     */
    public static BUAPI getApi() {
        if (instance == null) {
            throw new IllegalStateException("API is not loaded.");
        }
        return instance;
    }

    /**
     * Gets an instance of {@link BUAPI}, if it is loaded.
     *
     * <p>Unlike {@link BUCore#getApi}, this method will not throw an
     * {@link IllegalStateException} if an instance is not yet loaded, rather return
     * an empty {@link Optional}.
     *
     * @return an optional api instance
     */
    public static Optional<BUAPI> getApiSafe() {
        return Optional.ofNullable(instance);
    }

    /**
     * Registers an instance of the {@link BUAPI}.
     *
     * @param instance The instance to be registered.
     */
    static void registerApi(BUAPI instance) {
        BUCore.instance = instance;
    }

    /**
     * Removes the current api instance.
     */
    static void unregisterApi() {
        BUCore.instance = null;
    }

    private BUCore() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static void sendMessage(CommandSender sender, String message) {
        IConfiguration config = getApi().getLanguageManager().getLanguageConfiguration(getApi().getPlugin(), sender);

        sender.sendMessage(Utils.format(config.getString("prefix"), message));
    }
}