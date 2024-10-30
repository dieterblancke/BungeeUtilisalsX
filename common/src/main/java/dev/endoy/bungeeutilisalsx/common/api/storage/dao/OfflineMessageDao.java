package dev.endoy.bungeeutilisalsx.common.api.storage.dao;

import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
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
        HasMessagePlaceholders placeholders;

        public OfflineMessage( final Long id, final String languagePath, final HasMessagePlaceholders placeholders )
        {
            this.id = id;
            this.languagePath = languagePath;
            this.placeholders = placeholders;
        }
    }
}