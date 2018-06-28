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

    /**
     * @return Unique Identifier of the Bossbar.
     */
    UUID getUuid();

    /**
     * @return Bossbar color.
     */
    BarColor getColor();

    void setColor(BarColor color);

    /**
     * @return Bossbar style.
     */
    BarStyle getStyle();

    void setStyle(BarStyle style);

    /**
     * @return Bossbar progress.
     */
    float getProgress();

    void setProgress(float progress);

    /**
     * @return Bossbar Message.
     */
    BaseComponent[] getMessage();

    void setMessage(BaseComponent[] title);

    /**
     * @return Bossbar visibility.
     */
    boolean isVisible();

    void setVisible(boolean visible);

    @Deprecated
    void setMessage(String title);

    void addUser(User user);

    void removeUser(User user);

    /**
     * Check whether an user has the bossbar or not.
     *
     * @param user The user to check.
     * @return true if user receives bossbar, false if not.
     */
    boolean hasUser(User user);

    void clearUsers();

    /**
     * Unregisters the BossBar.
     */
    void unregister();
}