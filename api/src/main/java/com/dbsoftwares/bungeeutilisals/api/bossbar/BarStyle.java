package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;

import java.util.List;

public class BarStyle {

    public static final BarStyle SOLID;
    public static final BarStyle SIX_SEGMENTS;
    public static final BarStyle TEN_SEGMENTS;
    public static final BarStyle TWELF_SEGMENTS;
    public static final BarStyle TWENTY_SEGMENTS;

    public static final List<BarStyle> values;

    static {
        SOLID = new BarStyle(0);
        SIX_SEGMENTS = new BarStyle(1);
        TEN_SEGMENTS = new BarStyle(2);
        TWELF_SEGMENTS = new BarStyle(3);
        TWENTY_SEGMENTS = new BarStyle(4);

        values = Lists.newArrayList(SOLID, SIX_SEGMENTS, TEN_SEGMENTS, TWELF_SEGMENTS, TWENTY_SEGMENTS);
    }

    private int id;

    public BarStyle(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BarStyle[] values() {
        return values.toArray(new BarStyle[values.size()]);
    }

    public static BarStyle fromId(int action) {
        return values.stream().filter(a -> a.id == action).findFirst().orElse(SOLID);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BarStyle && ((BarStyle) obj).getId() == id);
    }
}