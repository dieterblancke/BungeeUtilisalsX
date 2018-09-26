package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.io.File;

public interface IAddonManager {

    void findAddons(File folder);

    void loadAddons();

}
