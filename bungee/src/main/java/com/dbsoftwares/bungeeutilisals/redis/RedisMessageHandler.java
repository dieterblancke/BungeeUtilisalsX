package com.dbsoftwares.bungeeutilisals.redis;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class RedisMessageHandler {

    private String channel;

}
