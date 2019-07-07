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

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class FriendRequestsSubCommand extends SubCommand {

    public FriendRequestsSubCommand() {
        super(
                "requests", 1, 2,
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.requests.aliases").split(", "))
        );
    }

    @Override
    public String getUsage() {
        return "/friends requests (in/out) [page]";
    }

    @Override
    public String getPermission() {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.requests.permission");
    }

    @Override
    public void onExecute(User user, String[] args) {
        final String type = args[0];
        final List<FriendRequest> allRequests;
        final String requestType;

        if (type.contains("out")) {
            allRequests = BUCore.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests(user.getUuid());
            requestType = "outgoing";
        } else if (type.contains("in")) {
            allRequests = BUCore.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests(user.getUuid());
            requestType = "incoming";
        } else {
            user.sendLangMessage("friends.requests.usage");
            return;
        }

        if (allRequests.isEmpty()) {
            user.sendLangMessage("friends.requests.no-requests");
            return;
        }

        final int pages = (int) Math.ceil((double) allRequests.size() / 15);
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

        if (maxNumber > allRequests.size()) {
            maxNumber = allRequests.size();
        }

        final List<FriendRequest> requests = allRequests.subList(minNumber, maxNumber);
        user.sendLangMessage(
                "friends.requests.head",
                "{previousPage}", previous,
                "{currentPage}", page,
                "{nextPage}", next,
                "{maxPages}", pages,
                "{type}", user.getLanguageConfig().getString("friends.requests." + requestType)
        );

        requests.forEach(request -> {
            final String targetName = requestType.equalsIgnoreCase("outgoing")
                    ? request.getFriendName() : request.getUserName();

            user.sendLangMessage(
                    "friends.requests.format." + requestType,
                    "{target}", targetName,
                    "{requestDate}", Utils.formatDate(request.getRequestedAt())
            );
        });
        user.sendLangMessage(
                "friends.requests.foot",
                "{requestAmount}", allRequests.size(), "{type}", requestType, "{type_lowercase}", requestType.toLowerCase()
        );
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
