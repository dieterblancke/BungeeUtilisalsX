package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayOutBossBar;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BossBar {

    private UUID uuid;
    private BarColor color;
    private BarStyle style;
    private float progress;
    private String title;
    private boolean visible;

    private List<User> users = Lists.newArrayList();

    public BossBar() {
        this(UUID.randomUUID(), BarColor.PINK, BarStyle.SOLID, 1F, "");
    }

    public BossBar(BarColor color, BarStyle style, float progress, String title) {
        this(UUID.randomUUID(), color, style, progress, title);
    }

    public BossBar(UUID uuid, BarColor color, BarStyle style, float progress, String title) {
        this.uuid = uuid;
        this.color = color;
        this.style = style;
        this.progress = progress;
        this.title = title;
        this.visible = true;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;

        PacketPlayOutBossBar packet;

        if (visible) {
            packet = new PacketPlayOutBossBar(uuid, BossBarAction.ADD, title, progress, color, style);
        } else {
            packet = new PacketPlayOutBossBar(uuid, BossBarAction.REMOVE);
        }

        users.forEach(user -> user.experimental().sendPacket(packet));
    }

    public void setColor(BarColor color) {
        this.color = color;

        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_STYLE, color, style)));
        }
    }

    public void setStyle(BarStyle style) {
        this.style = style;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_STYLE, color, style)));
        }
    }

    public void setHealth(float progress) {
        this.progress = progress;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_HEALTH, progress)));
        }
    }

    public void setMessage(String title) {
        this.title = title;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_TITLE, title)));
        }
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.ADD, title, progress, color, style));
        }
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);

            user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.REMOVE));
        }
    }

    public boolean hasUser(User user) {
        return users.contains(user);
    }
}