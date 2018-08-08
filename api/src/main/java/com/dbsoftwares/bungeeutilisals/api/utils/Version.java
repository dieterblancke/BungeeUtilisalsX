package com.dbsoftwares.bungeeutilisals.api.utils;

import lombok.Getter;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public enum Version {

    MINECRAFT_1_8(47),
    MINECRAFT_1_9(107),
    MINECRAFT_1_9_1(108),
    MINECRAFT_1_9_2(109),
    MINECRAFT_1_9_3(110),
    MINECRAFT_1_10(210),
    MINECRAFT_1_11(315),
    MINECRAFT_1_11_2(316),
    MINECRAFT_1_12(335),
    MINECRAFT_1_12_1(338),
    MINECRAFT_1_12_2(340),
    MINECRAFT_1_13(393);

    @Getter
    private int version;

    Version(int version) {
        this.version = version;
    }
}