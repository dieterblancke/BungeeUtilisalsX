package com.dbsoftwares.bungeeutilisals.api.utils.server;

/*
 * Created by DBSoftwares on 24 juni 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServerGroup {

    private String name;
    private boolean global;
    private List<String> servers;

}
