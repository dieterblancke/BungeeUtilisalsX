package dev.endoy.bungeeutilisalsx.common.api.event.events.redis;

import dev.endoy.bungeeutilisalsx.common.api.event.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode( callSuper = true )
public class RedisMessageEvent extends AbstractEvent
{

    private final String channel;
    private final String message;

}
