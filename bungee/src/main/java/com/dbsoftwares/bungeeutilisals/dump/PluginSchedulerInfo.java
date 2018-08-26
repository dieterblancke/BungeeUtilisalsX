package com.dbsoftwares.bungeeutilisals.dump;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PluginSchedulerInfo {

    private String plugin;
    private int running;
    private int total;

}