package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;

@Data
public abstract class Addon {

    private ProxyServer proxy;
    private BUAPI api;
    private AddonDescription description;

    protected void init(ProxyServer proxy, BUAPI api, AddonDescription description) {
        this.proxy = proxy;
        this.api = api;
        this.description = description;
    }

    public void onLoad() {
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
