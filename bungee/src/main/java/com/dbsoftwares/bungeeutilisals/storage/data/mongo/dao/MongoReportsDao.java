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

package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisals.api.utils.other.Report;

import java.util.List;

public class MongoReportsDao implements ReportsDao {

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    @Override
    public void addReport(Report report) {

    }

    @Override
    public void removeReport(long id) {

    }

    @Override
    public Report getReport(long id) {
        return null;
    }

    @Override
    public List<Report> getReports() {
        return null;
    }

    @Override
    public List<Report> getRecentReports(int days) {
        return null;
    }
}
