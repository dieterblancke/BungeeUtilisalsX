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

package com.dbsoftwares.bungeeutilisals.api.friends;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.ISection;
import net.md_5.bungee.api.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class FriendUtils {

    private FriendUtils() {
    }

    public static int getFriendsLimit(User user) {
        return getFriendsLimit(user.sender());
    }

    public static int getFriendsLimit(CommandSender sender) {
        final ISection limits = FileLocation.FRIENDS_CONFIG.getConfiguration().getSection("friendlimits");
        final List<String> permissions = sender.getPermissions().stream()
                .filter(perm -> perm.startsWith(limits.getString("permission")))
                .collect(Collectors.toList());

        int highestLimit = limits.getInteger("limits.default", 10);

        for (String permission : permissions) {
            final String[] parts = permission.split("\\.");
            final String lastPart = parts[parts.length - 1];

            int limit = 0;
            if (limits.exists("limits." + lastPart)) {
                limit = limits.getInteger("limits." + lastPart);
            } else if (MathUtils.isInteger(lastPart)) {
                limit = Integer.parseInt(lastPart);
            }

            if (limit > highestLimit) {
                highestLimit = limit;
            }
        }

        return highestLimit;
    }
}
