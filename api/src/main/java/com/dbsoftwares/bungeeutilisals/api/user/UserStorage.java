package com.dbsoftwares.bungeeutilisals.api.user;

/*
 * Created by DBSoftwares on 26 september 2017
 * Developer: Dieter Blancke
 * Project: centrixcore
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@Data
public class UserStorage {

    private UUID Uuid;
    private String userName;
    private String ip;
    private Language language;

    public void setDefaultsFor(ProxiedPlayer p) {
        setUuid(p.getUniqueId());
        setUserName(p.getName());
        setIp(Utils.getIP(p.getAddress()));
        setLanguage(BUCore.getApi().getLanguageManager().getDefaultLanguage());
    }
}