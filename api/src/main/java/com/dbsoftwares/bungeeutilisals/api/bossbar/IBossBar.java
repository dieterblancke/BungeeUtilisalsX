package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 25/04/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

public interface IBossBar {

    UUID getUuid();

    BarColor getColor();

    void setColor(BarColor color);

    BarStyle getStyle();

    void setStyle(BarStyle style);

    float getProgress();

    void setProgress(float progress);

    BaseComponent[] getMessage();

    void setMessage(BaseComponent[] title);

    boolean isVisible();

    void setVisible(boolean visible);

    BarFlag getFlag();

    void setFlag(BarFlag flag);

    @Deprecated
    void setMessage(String title);

    void addUser(User user);

    void removeUser(User user);

    boolean hasUser(User user);

    void unregister();
}