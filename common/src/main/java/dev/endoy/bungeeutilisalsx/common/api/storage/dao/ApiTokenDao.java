package dev.endoy.bungeeutilisalsx.common.api.storage.dao;

import lombok.Value;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ApiTokenDao
{

    void createApiToken( ApiToken token );

    Optional<ApiToken> findApiToken( String token );

    void removeApiToken( String token );

    List<ApiToken> getApiTokens();

    enum ApiPermission
    {
        ALL,
        FIND_USER,
        FIND_FRIENDS,
        FIND_BAN,
        FIND_MUTE,
        FIND_TRACK_DATA,
        FIND_KICK,
        FIND_WARN,
        FIND_REPORT,
        UPDATE_USER,
        CREATE_PUNISHMENT,
        REMOVE_PUNISHMENT
    }

    @Value
    class ApiToken
    {
        String apiToken;
        Date expireDate;
        List<ApiPermission> permissions;
    }
}
