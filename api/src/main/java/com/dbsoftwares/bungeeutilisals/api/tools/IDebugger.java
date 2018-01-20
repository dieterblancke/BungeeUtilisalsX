package com.dbsoftwares.bungeeutilisals.api.tools;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

public interface IDebugger {

    /**
     * This will print a debug message to the console IF the debug option is enabled.
     * This method uses the String.format() method for replacements.
     *
     * @param message      The message to be debugged.
     * @param replacements The replacements for the '%s' format in the message.
     */
    void debug(String message, Object... replacements);

    /**
     * This will print a debug message to the console.
     * This method uses the String.format() method for replacements.
     *
     * @param message      The message to be debugged.
     * @param replacements The replacements for the '%s' format in the message.
     */
    void forceDebug(String message, Object... replacements);

}