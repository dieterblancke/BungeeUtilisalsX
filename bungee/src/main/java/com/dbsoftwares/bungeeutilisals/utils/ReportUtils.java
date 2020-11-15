package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.utils.other.Report;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.util.List;
import java.util.UUID;

public class ReportUtils
{

    public static void handleReportsFor( final String accepter, final UUID uuid, final PunishmentType type )
    {
        final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

        if ( !config.getBoolean( "report.enabled" ) )
        {
            return;
        }

        if ( !config.getStringList( "report.accept_on_punishment" ).contains( type.toString() ) )
        {
            return;
        }

        final List<Report> reports = BUCore.getApi().getStorageManager().getDao().getReportsDao().getReports( uuid );
        reports.forEach( report -> report.accept( accepter ) );
    }
}
