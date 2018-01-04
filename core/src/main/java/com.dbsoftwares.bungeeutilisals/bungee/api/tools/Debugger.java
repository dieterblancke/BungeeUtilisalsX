package com.dbsoftwares.bungeeutilisals.bungee.api.tools;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.tools.IDebugger;
import com.dbsoftwares.bungeeutilisals.api.tools.ILoggers;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocations;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.util.logging.Level;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

public class Debugger implements IDebugger {

    @Override
    public void debug(String message, String... replacements) {
        if (BUCore.getApi().getConfig(FileLocations.CONFIG).getBoolean("debug")) {
            forceDebug(message, replacements);
        }
    }

    @Override
    public void forceDebug(String message, String... replacements) {
        BungeeUtilisals.getLog().log(Level.INFO, String.format(message, (Object[]) replacements));
        BUCore.getApi().getLoggers().ifPresent(loggers -> loggers.getDefaultLogger().log(ILoggers.LogLevel.INFO, message, replacements));
    }
}