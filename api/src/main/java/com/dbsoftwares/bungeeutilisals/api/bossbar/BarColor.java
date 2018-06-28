package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class BarColor {

    public static final BarColor PINK;
    public static final BarColor BLUE;
    public static final BarColor RED;
    public static final BarColor GREEN;
    public static final BarColor YELLOW;
    public static final BarColor PURPLE;
    public static final BarColor WHITE;

    public static final List<BarColor> values;

    static {
        PINK = new BarColor(0);
        BLUE = new BarColor(1);
        RED = new BarColor(2);
        GREEN = new BarColor(3);
        YELLOW = new BarColor(4);
        PURPLE = new BarColor(5);
        WHITE = new BarColor(6);

        values = Lists.newArrayList(PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE);
    }

    @Getter
    private int id;

    public BarColor(int id) {
        this.id = id;
    }

    public static BarColor[] values() {
        return values.toArray(new BarColor[values.size()]);
    }

    public static BarColor fromId(int action) {
        return values.stream().filter(a -> a.id == action).findFirst().orElse(PINK);
    }

    public static BarColor valueOf(String color) {
        if (color == null) {
            return PINK;
        }
        switch (color) {
            default:
            case "PINK":
                return PINK;
            case "BLUE":
                return BLUE;
            case "RED":
                return RED;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            case "PURPLE":
                return PURPLE;
            case "WHITE":
                return WHITE;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BarColor && ((BarColor) obj).getId() == id);
    }
}