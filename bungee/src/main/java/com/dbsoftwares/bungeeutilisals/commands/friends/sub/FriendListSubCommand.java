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

package com.dbsoftwares.bungeeutilisals.commands.friends.sub;

import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;

public class FriendListSubCommand extends SubCommand {

    public FriendListSubCommand() {
        super(
                "list", 0, 1,
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.list.aliases").split(", "))
        );
    }

    @Override
    public String getUsage() {
        return "/friends list [page]";
    }

    @Override
    public String getPermission() {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.list.permission");
    }

    @Override
    public void onExecute(User user, String[] args) {
        final List<FriendData> allFriends = user.getFriends();

        if (allFriends.isEmpty()) {
            user.sendLangMessage("friends.list.no-friends");
            return;
        }

        final int pages = (int) Math.ceil((double) allFriends.size() / 15);
        final int page;

        if (args.length >= 1) {
            if (MathUtils.isInteger(args[0])) {
                final int tempPage = Integer.parseInt(args[0]);

                if (tempPage > pages) {
                    page = pages;
                } else {
                    page = tempPage;
                }
            } else {
                page = 1;
            }
        } else {
            page = 1;
        }

        final int previous = page > 1 ? page - 1 : 1;
        final int next = page + 1 > pages ? pages : page + 1;

        int maxNumber = page * 10;
        int minNumber = maxNumber - 10;

        if (maxNumber > allFriends.size()) {
            maxNumber = allFriends.size();
        }

        final List<FriendData> friends = allFriends.subList(minNumber, maxNumber);
        user.sendLangMessage(
                "friends.list.head",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages
        );

        friends.forEach(friend ->
                user.sendLangMessage(
                        "friends.list.format",
                        "{friendName}", friend.getFriend(),
                        "{lastOnline}", friend.isOnline() ? "now" : Utils.formatDate(friend.getLastOnline()),
                        "{online}", friend.isOnline(),
                        "{friendSince}", Utils.formatDate(friend.getFriendSince())
                )
        );
        user.sendLangMessage("friends.list.foot", "{friendAmount}", allFriends.size());
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return null;
    }
}
