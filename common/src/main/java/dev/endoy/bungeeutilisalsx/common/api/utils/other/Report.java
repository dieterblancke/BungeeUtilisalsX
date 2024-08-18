package dev.endoy.bungeeutilisalsx.common.api.utils.other;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao.OfflineMessage;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Report implements HasMessagePlaceholders
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
                MessagePlaceholders.create()
                    .append( "id", id )
                    .append( "reported", reportedBy )
                    .append( "staff", accepter )
                    .append( this )
            )
        );
    }

    @Override
    public MessagePlaceholders getMessagePlaceholders()
    {
        return MessagePlaceholders.create()
            .append( "id", id )
            .append( "uuid", uuid )
            .append( "user", userName )
            .append( "reported", userName )
            .append( "reporter", reportedBy )
            .append( "reason", reason )
            .append( "server", server )
            .append( "date", Dao.formatDateToString( date ) )
            .append( "handled", handled )
            .append( "accepted", accepted );
    }
}
