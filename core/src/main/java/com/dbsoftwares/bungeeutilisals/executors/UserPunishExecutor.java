package com.dbsoftwares.bungeeutilisals.executors;

/*
 * Created by DBSoftwares on 12/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import net.md_5.bungee.api.ProxyServer;

import java.util.Date;
import java.util.List;

public class UserPunishExecutor implements EventExecutor {

    @Event
    public void updateMute(UserPunishEvent event) {
        if (event.isMute()) {
            event.getUser().ifPresent(user -> user.setMute(event.getInfo()));
        }
    }

    @Event
    public void executeActions(UserPunishEvent event) {
        if (!FileLocation.PUNISHMENTS_CONFIG.hasData(event.getType().toString())) {
            return;
        }
        List<PunishmentAction> actions = FileLocation.PUNISHMENTS_CONFIG.getData(event.getType().toString());

        for (PunishmentAction action : actions) {
            long amount;

            if (event.isUserPunishment()) {
                // UUID involved
                amount = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getPunishmentsSince(
                        event.getUUID().toString(),
                        event.getType(),
                        new Date(System.currentTimeMillis() - action.getUnit().toMillis(action.getTime()))
                );
            } else {
                // IP involved
                amount = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getPunishmentsSince(
                        event.getIp(),
                        event.getType(),
                        new Date(System.currentTimeMillis() - action.getUnit().toMillis(action.getTime()))
                );
            }

            if (amount >= action.getLimit()) {
                action.getActions().forEach(command -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                        ProxyServer.getInstance().getConsole(),
                        command.replace("%user%", event.getName())));
            }
        }
    }
}