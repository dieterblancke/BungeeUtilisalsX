package dev.endoy.bungeeutilisalsx.common.api.storage.dao;

import dev.endoy.bungeeutilisalsx.common.api.utils.other.Report;

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
