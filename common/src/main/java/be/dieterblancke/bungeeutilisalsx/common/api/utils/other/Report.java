package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Report
{

    private final long id;
    private final UUID uuid;
    private final String userName;
    private final String reportedBy;
    private final Date date;
    private final String server;
    private final String reason;
    private boolean handled;
    private boolean accepted;

    public void accept( final String accepter )
    {
        BuX.getInstance().getAbstractStorageManager().getDao().getReportsDao().handleReport( id, true );

        BuX.getApi().getStorageManager().getDao().getOfflineMessageDao().sendOfflineMessage(
                reportedBy,
                new OfflineMessage(
                        null,
                        "general-commands.report.accept.accepted",
                        "{id}", id,
                        "{reported}", userName,
                        "{staff}", accepter
                )
        );
    }
}
