package com.dbsoftwares.bungeeutilisals.api.utils.player;

/*
 * Created by DBSoftwares on 22 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.util.List;

public interface IPlayerUtils {

    int getPlayerCount(String server);

    List<String> getPlayers(String server);

}