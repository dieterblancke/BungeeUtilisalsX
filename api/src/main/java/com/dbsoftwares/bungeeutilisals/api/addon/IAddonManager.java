package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.io.File;
import java.util.Collection;

public interface IAddonManager {

    void findAddons(final File folder);

    void loadAddons();

    void enableAddons();

    void disableAddons();

    void disableAddon(final String addonName);

    Addon getAddon(final String addonName);

    Collection<Addon> getAddons();

}
