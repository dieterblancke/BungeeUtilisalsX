package com.dbsoftwares.bungeeutilisals.bungee.executors;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.command.CommandCreateEvent;
import com.dbsoftwares.bungeeutilisals.api.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.ProxyServer;

public class CommandCreateExecutor implements EventExecutor<CommandCreateEvent> {

    @Override
    public void onExecute(CommandCreateEvent event) {
        ProxyServer.getInstance().getPluginManager().registerCommand(BungeeUtilisals.getInstance(), event.getCommand());
    }
}