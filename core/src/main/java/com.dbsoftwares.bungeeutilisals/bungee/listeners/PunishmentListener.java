package com.dbsoftwares.bungeeutilisals.bungee.listeners;

/*
 * Created by DBSoftwares on 14/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PunishmentListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();
        String IP = Utils.getIP(connection.getAddress());

        BUAPI api = BUCore.getApi();
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        PunishmentInfo info = null;
        IConfiguration language = api.getLanguageManager().getConfig(BungeeUtilisals.getInstance(),
                api.getLanguageManager().getDefaultLanguage());

        if (executor.isBanned(uuid)) {
            info = executor.getBan(uuid);
        } else if (executor.isIPBanned(IP)) {
            info = executor.getIPBan(IP);
        } else if (executor.isTempBanned(uuid)) {
            info = executor.getTempBan(uuid);
        } else if (executor.isIPTempBanned(IP)) {
            // TODO ...
        }
        if (info != null) { // active punishment found
            if (info.isTemporary()) {
                if (info.getExpireTime() <= System.currentTimeMillis()) {
                    if (info.getType().equals(PunishmentType.TEMPBAN)) {
                        executor.removeTempBan(uuid);
                    } else {
                        executor.removeIPTempBan(IP);
                    }
                    return;
                }
            }
            String kick = Utils.formatList(language.getStringList("punishments." +
                    info.getType().toString().toLowerCase() + ".kick"), "\n");
            kick = executor.setPlaceHolders(kick, info);

            event.setCancelled(true);
            event.setCancelReason(Utils.format(kick));
        }
    }
}