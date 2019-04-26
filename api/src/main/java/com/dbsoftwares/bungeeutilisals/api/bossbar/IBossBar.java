/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.bossbar;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

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
     * @return Bossbar visibility.
     */
    boolean isVisible();

    void setVisible(boolean visible);

    String getMessage();

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