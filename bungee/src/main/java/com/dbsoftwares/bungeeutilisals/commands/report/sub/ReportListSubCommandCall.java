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
import com.dbsoftwares.bungeeutilisals.api.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.other.Report;

import java.util.List;

public class ReportListSubCommandCall implements CommandCall {

    @Override
    public void onExecute(User user, String[] args) {
        final ReportsDao reportsDao = BUCore.getApi().getStorageManager().getDao().getReportsDao();
        final List<Report> reports;
        final int page;

        if (args.length == 0) {
            reports = reportsDao.getActiveReports();
            page = 1;
        } else {
            final String action = args[0];

            if (action.equalsIgnoreCase("all")) {
                reports = reportsDao.getReports();
            } else {
                reports = reportsDao.getActiveReports();
            }

            if (args.length > 1 && MathUtils.isInteger(args[1])) {
                page = Integer.parseInt(args[1]);
            } else {
                page = 1;
            }
        }

        // TODO: send reports list
        // Paginating system ...

        /*<
         *         final List<AddonData> addons = BUCore.getApi().getAddonManager().getAllAddons();
         *         final int maxPages = (int) Math.ceil(addons.size() / 10.0);
         *         int page = 1;
         *         if (args.length != 0) {
         *             if (!MathUtils.isInteger(args[0])) {
         *                 user.sendLangMessage("no-number");
         *                 return;
         *             }
         *             page = Integer.parseInt(args[0]);
         *         }
         *
         *         if (page > maxPages) {
         *             user.sendLangMessage("general-commands.addon.list.wrong-page", "{maxpages}", maxPages);
         *             return;
         *         }
         *
         *         final int begin = ((page - 1) * 10);
         *         int end = begin + 10;
         *
         *         if (end > addons.size()) {
         *             end = addons.size();
         *         }
         *
         *         user.sendLangMessage("general-commands.addon.list.header", "{page}", page);
         *         final ChatMessage chatMessage = new ChatMessage();
         *
         *         for (int i = begin; i < end; i++) {
         *             final AddonData data = addons.get(i);
         *             final ChatColor color = BUCore.getApi().getAddonManager().isRegistered(data.getName()) ? ChatColor.GREEN : ChatColor.RED;
         *             final String message = user.buildLangMessage("general-commands.addon.list.item.text", "{id}", i + 1, "{addon}", color + data.getName());
         *
         *             chatMessage.addPartim(
         *                     message,
         *                     new HoverPartim(
         *                             HoverEvent.Action.SHOW_TEXT,
         *                             user.buildLangMessage("general-commands.addon.list.item.hover",
         *                                     "{id}", i + 1,
         *                                     "{name}", data.getName(),
         *                                     "{version}", data.getVersion(),
         *                                     "{author}", data.getAuthor(),
         *                                     "{reqDepends}", data.getRequiredDependencies() == null ? "None" : Utils.formatList(data.getRequiredDependencies(), ", "),
         *                                     "{optDepends}", data.getOptionalDependencies() == null ? "None" : Utils.formatList(data.getOptionalDependencies(), ", "),
         *                                     "{description}", data.getDescription()
         *                             )
         *                     ),
         *                     new ClickPartim(
         *                             ClickEvent.Action.RUN_COMMAND,
         *                             "/addon info " + data.getName()
         *                     )
         *             );
         *             if (i < end - 1) {
         *                 chatMessage.newLine();
         *             }
         *         }
         *         chatMessage.sendTo(user);
         */
    }
}
