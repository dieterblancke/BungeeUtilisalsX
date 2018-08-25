package com.dbsoftwares.bungeeutilisals.listeners;

/*
 * Created by DBSoftwares on 14/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
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
        PunishmentInfo info = null;
        IConfiguration language = api.getLanguageManager().getConfig(BungeeUtilisals.getInstance(), api.getLanguageManager().getDefaultLanguage());
        PunishmentDao dao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao();

        if (dao.isPunishmentPresent(PunishmentType.BAN, uuid, null, true)) {
            info = dao.getPunishment(PunishmentType.BAN, uuid, null);
        } else if (dao.isPunishmentPresent(PunishmentType.IPBAN, null, IP, true)) {
            info = dao.getPunishment(PunishmentType.IPBAN, null, IP);
        } else if (dao.isPunishmentPresent(PunishmentType.TEMPBAN, uuid, null, true)) {
            info = dao.getPunishment(PunishmentType.TEMPBAN, uuid, null);
        } else if (dao.isPunishmentPresent(PunishmentType.IPTEMPBAN, null, IP, true)) {
            info = dao.getPunishment(PunishmentType.IPTEMPBAN, null, IP);
        }
        if (info != null) { // active punishment found
            if (info.isTemporary()) {
                if (info.getExpireTime() <= System.currentTimeMillis()) {
                    if (info.getType().equals(PunishmentType.TEMPBAN)) {
                        dao.removePunishment(PunishmentType.TEMPBAN, uuid, null);
                    } else {
                        dao.removePunishment(PunishmentType.IPTEMPBAN, null, IP);
                    }
                    return;
                }
            }
            String kick = Utils.formatList(language.getStringList("punishments." + info.getType().toString().toLowerCase() + ".kick"), "\n");
            kick = api.getPunishmentExecutor().setPlaceHolders(kick, info);

            event.setCancelled(true);
            event.setCancelReason(Utils.format(kick));
        }
    }
}