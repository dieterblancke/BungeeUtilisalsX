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

package com.dbsoftwares.bungeeutilisals.commands.report.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.MessageQueue;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.other.QueuedMessage;
import com.dbsoftwares.bungeeutilisals.api.utils.other.Report;

import java.util.Optional;

public class ReportAcceptSubCommandCall implements CommandCall {

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length != 1) {
            user.sendLangMessage("general-commands.report.accept.usage");
            return;
        }

        if (!MathUtils.isLong(args[0])) {
            user.sendLangMessage("no-number");
            return;
        }

        final long id = Long.parseLong(args[0]);
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final ReportsDao reportsDao = dao.getReportsDao();
        final UserDao userDao = dao.getUserDao();

        if (!reportsDao.reportExists(id)) {
            user.sendLangMessage("general-commands.report.accept.not-found");
            return;
        }
        final Report report = reportsDao.getReport(id);

        reportsDao.handleReport(id, true);
        user.sendLangMessage(
                "general-commands.report.accept.updated",
                "{id}", report.getId()
        );

        final Optional<User> optionalUser = BUCore.getApi().getUser(report.getReportedBy());
        final MessageQueue<QueuedMessage> queue;

        if (optionalUser.isPresent()) {
            queue = optionalUser.get().getMessageQueue();
        } else {
            queue = BUCore.getApi().getStorageManager().getDao().createMessageQueue();
        }
        queue.add(new QueuedMessage(
                -1,
                report.getReportedBy(),
                new QueuedMessage.Message(
                        "general-commands.report.accept.accepted",
                        "{id}", report.getId(),
                        "{reported}", report.getUserName(),
                        "{staff}", user.getName()
                ),
                "NAME"
        ));
    }
}
