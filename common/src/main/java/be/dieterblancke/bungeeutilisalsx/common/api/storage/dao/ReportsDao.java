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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ReportsDao
{

    CompletableFuture<Void> addReport( Report report );

    CompletableFuture<Void> removeReport( long id );

    CompletableFuture<Report> getReport( long id );

    CompletableFuture<List<Report>> getReports();

    CompletableFuture<List<Report>> getReports( UUID uuid );

    CompletableFuture<List<Report>> getActiveReports();

    CompletableFuture<List<Report>> getHandledReports();

    CompletableFuture<List<Report>> getRecentReports( int days );

    CompletableFuture<Void> handleReport( long id, boolean accepted );

    CompletableFuture<List<Report>> getAcceptedReports();

    CompletableFuture<List<Report>> getDeniedReports();

    CompletableFuture<List<Report>> getReportsHistory( String name );

}
