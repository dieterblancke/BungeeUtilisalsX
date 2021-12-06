package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;

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

        BuX.getApi().getStorageManager().getDao().getReportsDao().getReports( uuid )
                .thenAccept( reports -> reports.forEach( report -> report.accept( accepter ) ) );
    }
}
