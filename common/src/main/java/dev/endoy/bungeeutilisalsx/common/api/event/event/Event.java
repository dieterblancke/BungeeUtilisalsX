package dev.endoy.bungeeutilisalsx.common.api.event.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Event
{

    int priority() default 0;

    boolean executeIfCancelled() default true;

}