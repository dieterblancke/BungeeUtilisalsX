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
public class SystemInfo {

    private String javaVersion;
    private String operatingSystem;
    private String platformName;
    private String platformVersion;
    private double TPS;
    private String maxMemory;
    private String freeMemory;
    private String totalMemory;
    private String onlineSince;
    private String totalSystemMemory;
    private String usedSystemMemory;
    private String freeSystemMemory;

}
