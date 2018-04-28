package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.google.common.collect.Lists;
import java.util.List;

public class BossBarAction {

    public static final BossBarAction ADD;
    public static final BossBarAction REMOVE;
    public static final BossBarAction UPDATE_HEALTH;
    public static final BossBarAction UPDATE_TITLE;
    public static final BossBarAction UPDATE_STYLE;
    public static final BossBarAction UPDATE_FLAGS;

    public static final List<BossBarAction> values;

    static {
        ADD = new BossBarAction(0);
        REMOVE = new BossBarAction(1);
        UPDATE_HEALTH = new BossBarAction(2);
        UPDATE_TITLE = new BossBarAction(3);
        UPDATE_STYLE = new BossBarAction(4);
        UPDATE_FLAGS = new BossBarAction(5);

        values = Lists.newArrayList(ADD, REMOVE, UPDATE_HEALTH, UPDATE_TITLE, UPDATE_STYLE, UPDATE_FLAGS);
    }

    private int id;

    public BossBarAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BossBarAction[] values() {
        return values.toArray(new BossBarAction[values.size()]);
    }

    public static BossBarAction fromId(int action) {
        return values.stream().filter(a -> a.id == action).findFirst().orElse(ADD);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof BossBarAction && ((BossBarAction) obj).getId() == id);
    }
}