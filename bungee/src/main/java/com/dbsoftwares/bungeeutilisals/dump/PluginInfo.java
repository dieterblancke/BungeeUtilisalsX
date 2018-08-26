package com.dbsoftwares.bungeeutilisals.dump;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PluginInfo {

    private String name;
    private String version;
    private String author;
    private Set<String> depends;
    private Set<String> softDepends;
    private String description;

}
