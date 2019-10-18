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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisals.api.utils.other.Report;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.*;
import java.util.function.Consumer;

public class MongoReportsDao implements ReportsDao {

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private MongoDBStorageManager manager() {
        return (MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement();
    }

    private MongoDatabase db() {
        return manager().getDatabase();
    }

    @Override
    public void addReport(Report report) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put("_id", manager().getNextSequenceValue("reportid"));
        data.put("uuid", report.getUuid().toString());
        data.put("reported_by", report.getReportedBy());
        data.put("date", new Date());
        data.put("handled", report.isHandled());
        data.put("server", report.getServer());
        data.put("reason", report.getReason());

        db().getCollection(format("{reports-table}")).insertOne(new Document(data));
    }

    @Override
    public void removeReport(long id) {
        db().getCollection(format("{reports-table}")).deleteOne(Filters.eq("_id", id));
    }

    @Override
    public Report getReport(long id) {
        final Report report;
        final Document document = db().getCollection(format("{reports-table}")).find(Filters.eq("_id", id)).limit(1).first();

        if (document == null || document.isEmpty()) {
            report = null;
        } else {
            report = getReport(document);
        }

        return report;
    }


    private Report getReport(final Document document) {
        final MongoCollection<Document> userColl = db().getCollection(format("{users-table}"));
        final Document user = userColl.find(Filters.eq("uuid", document.getString("uuid"))).first();

        return new Report(
                document.getInteger("_id"),
                UUID.fromString(document.getString("uuid")),
                user.getString("username"),
                document.getString("reported_by"),
                document.getDate("date"),
                document.getString("server"),
                document.getString("reason"),
                document.getBoolean("handled")
        );
    }

    @Override
    public List<Report> getReports() {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection(format("{reports-table}")).find()
                .forEach((Consumer<? super Document>) doc -> reports.add(getReport(doc)));

        return reports;
    }

    @Override
    public List<Report> getActiveReports() {
        return getHandledReports(true);
    }

    @Override
    public List<Report> getHandledReports() {
        return getHandledReports(false);
    }

    private List<Report> getHandledReports(final boolean handled) {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection(format("{reports-table}")).find(Filters.eq("handled", handled))
                .forEach((Consumer<? super Document>) doc -> reports.add(getReport(doc)));

        return reports;
    }

    @Override
    public List<Report> getRecentReports(int days) {
        final List<Report> reports = Lists.newArrayList();
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        db().getCollection(format("{reports-table}")).find(Filters.gte("date", calendar.getTime()))
                .forEach((Consumer<? super Document>) doc -> reports.add(getReport(doc)));

        return reports;
    }
}
