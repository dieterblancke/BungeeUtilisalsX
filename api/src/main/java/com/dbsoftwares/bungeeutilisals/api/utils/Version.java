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

    public static Version getVersion(int version) {
        for (Version v : values()) {
            if (v.getVersion() == version) {
                return v;
            }
        }
        return null;
    }

    public static Version latest() {
        Version version = null;
        for (Version v : values()) {
            if (version == null || version.getVersion() < v.getVersion()) {
                version = v;
            }
        }
        return version;
    }

    @Override
    public String toString() {
        return super.toString().replace("MINECRAFT_", "").replace("_", ".");
    }
}