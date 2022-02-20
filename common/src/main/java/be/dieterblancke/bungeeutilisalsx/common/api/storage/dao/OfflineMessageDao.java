package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import lombok.Value;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OfflineMessageDao
{

    CompletableFuture<List<OfflineMessage>> getOfflineMessages( String username );

    CompletableFuture<Void> sendOfflineMessage( String username, OfflineMessage message );

    CompletableFuture<Void> deleteOfflineMessage( Long id );

    @Value
    class OfflineMessage
    {
        Long id;
        String languagePath;
        Object[] placeholders;

        public OfflineMessage( final Long id, final String languagePath, final Object... placeholders )
        {
            this.id = id;
            this.languagePath = languagePath;
            this.placeholders = placeholders;
        }
    }
}