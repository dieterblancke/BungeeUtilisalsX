package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 25/04/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class BarFlag {

    public static final BarFlag DARKEN_SKY;
    public static final BarFlag PLAY_BOSS_MUSIC;
    public static final BarFlag CREATE_FOG;

    public static final List<BarFlag> values;

    static {
        DARKEN_SKY = new BarFlag((short) 0x1);
        PLAY_BOSS_MUSIC = new BarFlag((short) 0x2);
        CREATE_FOG = new BarFlag((short) 0x3);

        values = Lists.newArrayList(DARKEN_SKY, PLAY_BOSS_MUSIC, CREATE_FOG);
    }

    @Getter
    private short id;

    public BarFlag(short id) {
        this.id = id;
    }

    public static BarFlag[] values() {
        return values.toArray(new BarFlag[values.size()]);
    }

    public static BarFlag fromId(short id) {
        return values.stream().filter(a -> a.id == id).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BarFlag && ((BarFlag) obj).getId() == id);
    }
}