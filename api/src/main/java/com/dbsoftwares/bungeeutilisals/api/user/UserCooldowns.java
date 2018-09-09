/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.user;

import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class UserCooldowns {

    private HashMap<String, Long> map = Maps.newHashMap();

    /**
     * Checks if a cooldown is registered.
     *
     * @param name The cooldown you want to check.
     * @return true if registered, false if not.
     */
    public boolean isRegistered(String name) {
        return map.containsKey(name);
    }

    /**
     * Registers a new cooldown into the map.
     *
     * @param type The name of the cooldown.
     */
    public void register(String type) {
        map.put(type, 0L);
    }

    /**
     * @param type The String (key) of which you want to get the delay.
     * @return System.currentTimeMillis() + cooldown time in Milliseconds
     */
    public Long getExpire(String type) {
        if (!isRegistered(type)) {
            register(type);
        }
        return map.get(type);
    }

    /**
     * @param type The String (key) of which you want to get the cooldown.
     * @return A long of the time - currentTimeMillis
     */
    public Long getLeftTime(String type) {
        if (!isRegistered(type)) {
            register(type);
        }
        return map.get(type) - System.currentTimeMillis();
    }

    /**
     * @param type The String (key) of which you want to get the cooldown.
     * @return True if cooldown went over, false if still in cooldown.
     */
    public Boolean canUse(String type) {
        if (!isRegistered(type)) {
            register(type);
        }
        return System.currentTimeMillis() >= map.get(type);
    }

    /**
     * Puts in a new time value in the Cooldowns.
     *
     * @param type The String (key) to be updated.
     * @param unit The timeunit ...
     * @param time The amount of time ...
     */
    public void updateTime(String type, TimeUnit unit, int time) {
        map.put(type, System.currentTimeMillis() + unit.toMillis(time));
    }

    /**
     * Used to clear cooldown settings.
     */
    public void remove() {
        map.clear();
    }
}