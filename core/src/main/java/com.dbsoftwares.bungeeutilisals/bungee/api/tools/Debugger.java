package com.dbsoftwares.bungeeutilisals.bungee.api.tools;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.tools.IDebugger;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

/*
 * Created by DBSoftwares on 04 01 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

public class Debugger implements IDebugger {

    @Override
    public void debug(String message, Object... replacements) {
        if (BUCore.getApi().getConfig(FileLocation.CONFIG).getBoolean("debug")) {
            forceDebug(message, replacements);
        }
    }

    @Override
    public void forceDebug(String message, Object... replacements) {
        BungeeUtilisals.log(String.format(message, (Object[]) replacements));
    }
}