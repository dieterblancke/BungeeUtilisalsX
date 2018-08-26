package com.dbsoftwares.bungeeutilisals.dump;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Dump {

    private String plugin;
    private SystemInfo systemInfo;
    private List<PluginInfo> plugins;
    private Map<String, Map<String, Object>> configurations;
    private Map<String, Map<String, Object>> languages;
}