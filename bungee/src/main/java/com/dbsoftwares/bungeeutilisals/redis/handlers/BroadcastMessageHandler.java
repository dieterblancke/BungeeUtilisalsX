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

package com.dbsoftwares.bungeeutilisals.redis.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.APIAnnouncement;

import java.util.stream.Stream;

public class BroadcastMessageHandler extends RedisMessageHandler<APIAnnouncement> {

    public BroadcastMessageHandler() {
        super(APIAnnouncement.class);
    }

    @Override
    public void handle(final APIAnnouncement announcement) {
        Stream<User> users = BUCore.getApi().getUsers().stream();

        if (announcement.getPermission() != null) {
            users = users.filter(user -> user.getParent().hasPermission(announcement.getPermission()));
        }

        if (announcement.isLanguage()) {
            final ILanguageManager languageManager = announcement.isPluginLanguageManager()
                    ? BUCore.getApi().getLanguageManager()
                    : BUCore.getApi().getAddonManager().getLanguageManager();

            users.forEach(user -> LanguageUtils.sendLangMessage(
                    languageManager, announcement.getPlugin(), user, announcement.getMessage(), announcement.getPlaceHolders()
            ));
        } else {
            if (announcement.getPrefix() == null) {
                users.forEach(user -> user.sendMessage(announcement.getMessage()));
            } else {
                users.forEach(user -> user.sendMessage(announcement.getPrefix(), announcement.getMessage()));
            }
        }
    }
}
