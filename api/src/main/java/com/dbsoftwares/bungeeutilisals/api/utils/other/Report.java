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

package com.dbsoftwares.bungeeutilisals.api.utils.other;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.MessageQueue;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Report {

    private final long id;
    private final UUID uuid;
    private final String userName;
    private final String reportedBy;
    private final Date date;
    private final String server;
    private final String reason;
    private boolean handled;
    private boolean accepted;

    public void accept(final String accepter) {
        BUCore.getApi().getStorageManager().getDao().getReportsDao().handleReport(id, true);

        final Optional<User> optionalUser = BUCore.getApi().getUser(reportedBy);
        final MessageQueue<QueuedMessage> queue;

        if (optionalUser.isPresent()) {
            queue = optionalUser.get().getMessageQueue();
        } else {
            queue = BUCore.getApi().getStorageManager().getDao().createMessageQueue();
        }
        queue.add(new QueuedMessage(
                -1,
                reportedBy,
                new QueuedMessage.Message(
                        "general-commands.report.accept.accepted",
                        "{id}", id,
                        "{reported}", userName,
                        "{staff}", accepter
                ),
                "NAME"
        ));
    }
}
