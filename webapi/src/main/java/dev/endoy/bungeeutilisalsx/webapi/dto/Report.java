package dev.endoy.bungeeutilisalsx.webapi.dto;

import lombok.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Value
public class Report
{
    String userId;
    String reporterId;
    LocalDateTime date;
    String server;
    String reason;
    boolean handled;
    boolean accepted;

    public static Report of( final dev.endoy.bungeeutilisalsx.common.api.utils.other.Report report )
    {
        return new Report(
            report.getUserName(),
            report.getReportedBy(),
            new Timestamp( report.getDate().getTime() ).toLocalDateTime(),
            report.getServer(),
            report.getReason(),
            report.isHandled(),
            report.isAccepted()
        );
    }
}
